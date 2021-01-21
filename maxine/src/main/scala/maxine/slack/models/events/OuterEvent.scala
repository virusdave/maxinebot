package maxine
package slack.models.events

import io.circe.{Codec, Decoder, DecodingFailure, HCursor, Json}
import io.circe.generic.semiauto.deriveCodec

sealed trait OuterEvent extends Product with Serializable

case class WrappedSlackEvent(
    token: String,
    team_id: String,
    api_app_id: String,
    event: SlackEvent,
    `type`: String,
    authorizations: Json,
    event_context: String,
    event_id: String,
    event_time: Long,
) extends OuterEvent

// TODO(Dave): Make unknown (or unparsable) Events non-fatal by uncommenting the below

//case class WrappedUnknownSlackEvent(
//    token: String,
//    team_id: String,
//    api_app_id: String,
//    event: Json,
//    `type`: String,
//    authorizations: Json,
//    event_context: String,
//    event_id: String,
//    event_time: Long,
//) extends OuterEvent

object OuterEvent {
  implicit val wrappedSlackEventFmt: Codec[WrappedSlackEvent] = deriveCodec[WrappedSlackEvent]
//  implicit val wrappedUnknownSlackEventFmt: Codec[WrappedUnknownSlackEvent] = deriveCodec[WrappedUnknownSlackEvent]

  implicit val outerEventReads: Decoder[OuterEvent] = (c: HCursor) => {
    val event: Either[DecodingFailure, OuterEvent] = for {
      etype <- c.downField("type").as[String]
      reason <- c.downField("reason").as[Option[String]]
      result <- etype match {
        case "event_callback" => c.as[WrappedSlackEvent]
          // Swap case statements to make unparsable or unknown events non-fatal
        //case "event_callback" => c.as[WrappedSlackEvent].swap.flatMap(_ => c.as[WrappedUnknownSlackEvent].swap).swap

        case t: String => Left(DecodingFailure(s"Invalid outer type property: $t", List.empty))
      }
    } yield result
    event
  }
}