package maxine
package slack.models.events

import io.circe.{Codec, Decoder, DecodingFailure, HCursor, Json}
import io.circe.generic.semiauto.{deriveCodec, deriveDecoder}

sealed trait SocketEvent extends Product with Serializable
sealed abstract class AckEvent extends SocketEvent {
  def envelope_id: String
  def accepts_response_payload: Boolean
}

/** When this is received, the websocket should be closed and recreated */
case class DisconnectRefreshRequested(debug_info: DisconnectDebugInfo) extends SocketEvent
case class Disconnect(reason: String, debug_info: DisconnectDebugInfo) extends SocketEvent

case class DisconnectDebugInfo(host: String)

case class Hello(
    connection_info: Json,
    num_connections: Int,
    debug_info: Json,
) extends SocketEvent

case class EventsApiEvent(
    envelope_id: String,
    accepts_response_payload: Boolean,
    retry_attempt: Option[Int],
    retry_reason: Option[String],
    payload: OuterEvent,
) extends AckEvent


case class SlashCommand(
    envelope_id: String,
    accepts_response_payload: Boolean,
    retry_attempt: Option[Int],
    retry_reason: Option[String],
    payload: SocketEvent.SlashCommandPayload,
) extends AckEvent

case class Interactive(
    envelope_id: String,
    accepts_response_payload: Boolean,
    retry_attempt: Option[Int],
    retry_reason: Option[String],
    payload: SocketEvent.InteractivePayload,
) extends AckEvent

object SocketEvent {
  // TODO(Dave): Model these!
  type SlashCommandPayload = Json
  type InteractivePayload = Json

  implicit val helloFmt: Codec[Hello] = deriveCodec[Hello]
  implicit val debugInfoFmt: Codec[DisconnectDebugInfo] = deriveCodec[DisconnectDebugInfo]
  implicit val disconnectFmt: Codec[Disconnect] = deriveCodec[Disconnect]
  implicit val disconnectRefreshRequestedFmt: Codec[DisconnectRefreshRequested] = deriveCodec[DisconnectRefreshRequested]
  implicit val eventsApiEventFmt: Decoder[EventsApiEvent] = deriveDecoder[EventsApiEvent]
  implicit val slashCommandFmt: Codec[SlashCommand] = deriveCodec[SlashCommand]
  implicit val blockKitFmt: Codec[Interactive] = deriveCodec[Interactive]

  implicit val socketEventReads: Decoder[SocketEvent] = (c: HCursor) => {
    val event: Either[DecodingFailure, SocketEvent] = for {
      etype <- c.downField("type").as[String]
      reason <- c.downField("reason").as[Option[String]]
      result <- etype match {
        case "disconnect" if reason.contains("refresh_requested") => c.as[DisconnectRefreshRequested]
        case "disconnect" => c.as[Disconnect]

        case "events_api" => c.as[EventsApiEvent]
        case "hello" => c.as[Hello]
        case "interactive" => c.as[Interactive]
        case "slash_commands" => c.as[SlashCommand]

        case t: String => Left(DecodingFailure(s"Invalid socket event type property: $t", List.empty))
      }
    } yield result
    event
  }
}