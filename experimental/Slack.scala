package experimental

import java.io.File
import slack.{AccessToken, SlackEnv, SlackError, api}
import slack.api.chats._
import slack.api.conversations._
import slack.api.realtime
import slack.api.users._
import slack.client.SlackClient
import slack.realtime.SlackRealtimeClient
import slack.realtime.SlackRealtimeClient.Service
import slack.realtime.models.{Message, SendMessage, UserTyping}
import sttp.client._
import sttp.client.asynchttpclient.zio.{AsyncHttpClientZioBackend, SttpClient, ZioWebSocketHandler}
import sttp.client.ws.WebSocket
import zio._
import zio.clock._
import zio.config._
import zio.config.ConfigDescriptor._
import zio.config.typesafe.TypesafeConfig
import zio.console._
import zio.duration._
import zio.stream.ZStream

/** Use the modern websockets slack API rather than the older RTM one (which is deprecated for new Slack apps) */
object NewSlackRealtimeClient {

  // TODO(Dave): This requires different credentials than the normal SttpClient
//  def live: ZLayer[SttpClient, Nothing, SlackRealtimeClient] =
//    ZLayer.fromFunction[SttpClient, SlackRealtimeClient.Service](
//      client =>
//        new Service {
//          override def openWebsocket: ZIO[SlackEnv, SlackError, WebSocket[Task]] =
//            for {
//              // This particular API call requires a custom bot authentication header
//              // See:
//              //   https://api.slack.com/methods/apps.connections.open#arg_token
//              url <- api.sendM(api.request("apps.connections.open")) >>= api.as[String]("url")
//              r <- ZioWebSocketHandler().flatMap { handler =>
//                client.get.openWebsocket(basicRequest.get(uri"$url"), handler)
//              }
//            } yield r.result
//        }
//    )

  val fromConfig: ZLayer[SttpClient with Slack.AppConfig, Nothing, SlackRealtimeClient] =
    ZLayer.fromFunction[SttpClient with Slack.AppConfig, SlackRealtimeClient.Service](
      env =>
        new Service {
          override def openWebsocket: ZIO[SlackEnv, SlackError, WebSocket[Task]] = for {
            handler <- ZioWebSocketHandler()
            wsUri = env.get[Slack.SlackConfig].wsUrl
            // TODO(Dave): Replace the `.get` here.
            ws <- env.get[SttpClient.Service].openWebsocket(basicRequest.get(uri"${wsUri.get}"), handler).map(_.result)
          } yield ws
        }
    )
}

object Slack extends App {
  //***********************************
  // For IDE navigation
  private def _IDE(): Unit = {
    type E1 = SlackEnv
    type E2 = SlackRealtimeClient
    val E3 = ZConfig
  }
  //***********************************

  case class SlackConfig(accessToken: String, websocketAppToken: String, wsUrl: Option[String])
  type AppConfig = Has[SlackConfig]

  private val configuration: ConfigDescriptor[SlackConfig] = (
    string("accessToken") ??
      "Access token for the bot.  Should start with 'xoxb-' most likely." |@|
    string("websocketAppToken") ??
      "Access app token for the bot.  Should start with 'xapp-' most likely." |@|
    string("wsUrl").optional ??
      "If present, the websocket URL to use for realtime events.  Should start with 'wss://' most likely."
  )(SlackConfig.apply, SlackConfig.unapply)


  private val slackLayer = {
    ((ZIO.access[AppConfig](_.get.accessToken) >>= AccessToken.make).toLayer ++
      AsyncHttpClientZioBackend.layer() ++
      ZLayer.requires[AppConfig]
    ) >+> (
      SlackClient.live ++
        NewSlackRealtimeClient.fromConfig
    )
  }

  private val getWebsocketUrl: ZIO[SlackClient with AppConfig, Throwable, String] = {
    val act = api.sendM(api.request("apps.connections.open")) >>= api.as[String]("url")

    // This particular API call requires a custom bot authentication header
    // See:
    //   https://api.slack.com/methods/apps.connections.open#arg_token
    val botLayer: RLayer[AppConfig, Has[AccessToken]] =
      (ZIO.access[AppConfig](_.get.websocketAppToken) >>= AccessToken.make).toLayer

    act.provideSomeLayer[SlackClient with AppConfig].apply(botLayer)
  }

  private def handleWebsocket(ws: WebSocket[Task]): ZIO[Console, Throwable, Unit] =
    ws
      .receiveText()
      .tap {
        case Left(value) => putStrLn(s"  websocket closed, bailing on fiber")
        case Right(value) => putStrLn(s"  received event: ${value}")
      }
      .repeatUntil(_.isLeft) *> ZIO.unit

  private def appEnv(f: File) = {
    TypesafeConfig.fromHoconFile(f, nested("experimental")(nested("slack")(configuration))) >+> slackLayer
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (for {
      cfg <- ZIO.access[AppConfig](_.get)
      _ <- putStrLn(s"Loaded config: ${cfg}")
      _ <- postChatMessage("maxine-test-spam", "Hello, world!")
      channels <- listConversations(excludeArchived = true.some)
      rmt = channels.channels.find(_.name == "reclaim-my-time")
      memberIds <- getConversationMembers(rmt.get.id)
      members <- ZIO.foreach(memberIds)(getUserInfo)
      _ <- ZIO.foreach_(members)(m => putStrLn(s"${m.id} ${m.profile.get.real_name} ${m.profile.get.email}"))

      _ <- realtime.connect(ZStream(SendMessage("maxine-test-spam", "Hi realtime!"))).use { receiver =>
        receiver.collectM {
          case UserTyping(channel, user) => putStrLn(s"User $user is typing in $channel")
          case m: Message => putStrLn(s"heard ${m}")
          case _ => ZIO.unit
        }.runDrain.race(sleep(1.minute))
      }
    } yield ())
      .provideCustomLayer(appEnv(new File(args.headOption.getOrElse("./config.cfg"))))
      .exitCode
  }
}