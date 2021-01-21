package maxine
package slack.models.events

import io.circe.Decoder.Result
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import _root_.slack.models.{ App, Attachment, Channel, Im, ReactionItem, SlackFile, User }

// Marker trait for slack events we receive from the events API
sealed trait SlackEvent extends Serializable with Product

//********************************************************************************
// Messages
//********************************************************************************
// TODO: Message Sub-types
case class Message(ts: String,
    channel: String,
    user: String,
    text: String,
    is_starred: Option[Boolean],
    thread_ts: Option[String])
  extends SlackEvent

case class EditMessage(user: Option[String], text: String, ts: String)

case class ReplyMarker(user: String, ts: String)

case class ReplyMessage(user: String, text: String, thread_ts: String, reply_count: Int, replies: Seq[ReplyMarker])

case class MessageChanged(
    message: EditMessage,
    previous_message: EditMessage,
    ts: String,
    event_ts: String,
    channel: String)
  extends SlackEvent

case class MessageDeleted(ts: String, deleted_ts: String, event_ts: String, channel: String) extends SlackEvent

case class MessageReplied(ts: String, event_ts: String, channel: String, message: ReplyMessage) extends SlackEvent

case class BotMessage(ts: String,
    channel: String,
    text: String,
    bot_id: String,
    username: Option[String],
    attachments: Option[Seq[Attachment]])
  extends SlackEvent

// TODO: Message Sub-types
case class MessageWithSubtype(ts: String,
    channel: String,
    user: String,
    text: String,
    is_starred: Option[Boolean],
    messageSubType: MessageSubtype)
  extends SlackEvent

sealed trait MessageSubtype {
  def subtype: String
}

object MessageSubtypes {

  // Fallback for unhandled message sub-types
  case class UnhandledSubtype(subtype: String) extends MessageSubtype

  case class MeMessage(subtype: String) extends MessageSubtype {
    //val subtype = "me_message"
  }

  case class ChannelNameMessage(oldname: String, name: String) extends MessageSubtype {
    val subtype = "channel_name"
  }

  case class FileShareMessage(file: SlackFile) extends MessageSubtype {
    val subtype = "file_share"
  }

}



//********************************************************************************
// All non-message top-level events
//********************************************************************************
sealed trait TopLevelEvent

case class AppRateLimited(
    token: String,
    team_id: String,
    minute_rate_limited: Long,
    api_app_id: String,
) extends TopLevelEvent

case class UrlVerification(`type`: String, token: String, challenge: String) extends TopLevelEvent

//********************************************************************************
// All non-message metadata-wrapped events
//********************************************************************************

case class View(
    id: String,
    team_id: String,
    `type`: String,
    blocks: Json,  // TODO(Dave): Tighten this up?
    private_metadata: String,
    callback_id: String,
    state: Json,  // TODO(Dave): Tighten this up?
    hash: String,
    clear_on_close: Boolean,
    notify_on_close: Boolean,
    root_view_id: String,
    app_id: String,
    external_id: String,
    app_installed_team_id: String,
    bot_id: String,
)
object View { implicit val codec = deriveCodec[View] }

case class AppHomeOpened(
    user: String,
    channel: String,
    event_ts: String,
    tab: String,
    view: View,
) extends SlackEvent

case class AppMention(
    user: String,
    text: String,
    ts: String,
    channel: String,
    event_ts: String,
) extends SlackEvent

case class AppRequested(
    app_request: Json,  // TODO(Dave): Tighten this up?
) extends SlackEvent

case class AppUninstalled() extends SlackEvent

case class CallRejected(
    call_id: String,
    user_id: String,
    channel_id: String,
    external_unique_id: String,
) extends SlackEvent

case class ChannelArchive(channel: String, user: String) extends SlackEvent

case class ChannelCreated(channel: Channel) extends SlackEvent

case class ChannelDeleted(channel: String) extends SlackEvent

case class ChannelHistoryChanged(latest: Long, ts: String, event_ts: String) extends SlackEvent

case class ChannelLeft(channel: String) extends SlackEvent

case class ChannelRename(channel: Channel) extends SlackEvent

case class ChannelShared(connected_team_id: String, channel: String, event_ts: String) extends SlackEvent

case class ChannelUnarchive(channel: String, user: String) extends SlackEvent

case class ChannelUnshared(
    previously_connected_team_id: String,
    channel: String,
    is_ext_shared: Boolean,
    event_ts: String,
) extends SlackEvent

case class DndUpdated(user: String, dnd_status: DndStatus) extends SlackEvent

case class DndUpdatedUser(`type`: String, user: String, dnd_status: DndStatus, event_ts: String) extends SlackEvent

case class DndStatus(dnd_enabled: Boolean, next_dnd_start_ts: Long, next_dnd_end_ts: Long)

case class EmailDomainChanged(email_domain: String, event_ts: String) extends SlackEvent

// TODO(Dave): This one has subtypes!
case class EmojiChanged(event_ts: String) extends SlackEvent

case class FileChange(file_id: String) extends SlackEvent

case class FileCommentAdded(file_id: String,
    comment: Json // TODO: SlackComment?
) extends SlackEvent

case class FileCommentDeleted(file_id: String, comment: String) extends SlackEvent

case class FileCommentEdited(file_id: String,
    comment: Json // TODO: SlackComment?
) extends SlackEvent

case class FileCreated(file_id: String) extends SlackEvent

case class FileDeleted(file_id: String, event_ts: String) extends SlackEvent

case class FilePublic(file_id: String) extends SlackEvent

case class FilePrivate(file: String) extends SlackEvent

case class FileShared(file_id: String) extends SlackEvent

case class FileUnshared(file_id: String) extends SlackEvent

case class GridMigrationFinished(enterprise_id: String) extends SlackEvent

case class GridMigrationStarted(enterprise_id: String) extends SlackEvent

case class GroupArchive(channel: String) extends SlackEvent

case class GroupClose(user: String, channel: String) extends SlackEvent

case class GroupDeleted(channel: String) extends SlackEvent

case class GroupHistoryChanged(latest: Long, ts: String, event_ts: String) extends SlackEvent

case class GroupLeft(channel: String) extends SlackEvent

case class GroupOpen(user: String, channel: String) extends SlackEvent

case class GroupRename(channel: Channel) extends SlackEvent

case class GroupUnarchive(channel: String) extends SlackEvent

case class ImClose(user: String, channel: String) extends SlackEvent

case class ImCreated(user: String, channel: Im) extends SlackEvent

case class ImHistoryChanged(latest: Long, ts: String, event_ts: String) extends SlackEvent

case class ImOpen(user: String, channel: String) extends SlackEvent

case class Team(id: String, name: String, domain: String)
case class InviteRequest(
    id: String,
    email: String,
    date_created: Long,
    requester_ids: List[String],
    channel_ids: List[String],
    invite_type: String,
    real_name: String,
    date_expire: Long,
    request_reason: String,
    team: Team)
case class InviteRequested(invite_request: InviteRequest) extends SlackEvent

case class Link(domain: String, url: String)
case class LinkShared(
    channel: String,
    is_bot_user_member: Boolean,
    user: String,
    message_ts: String,
    thread_ts: String,
    links: List[Link],
) extends SlackEvent

case class MemberJoinedChannel(user: String, channel: String, inviter: Option[String]) extends SlackEvent

case class MemberLeftChannel(user: String, channel: String) extends SlackEvent

// Format of event is tbd
case class PinAdded(`type`: String) extends SlackEvent

// Format of event is tbd
case class PinRemoved(`type`: String) extends SlackEvent

case class ReactionAdded(
    reaction: String,
    item: ReactionItem,
    event_ts: String,
    user: String,
    item_user: Option[String],
) extends SlackEvent

case class ReactionRemoved(
    reaction: String,
    item: ReactionItem,
    event_ts: String,
    user: String,
    item_user: Option[String],
) extends SlackEvent

case class StarAdded(user: String,
    item: Json, // TODO: Different item types -- https://api.slack.com/methods/stars.list
    event_ts: String)
  extends SlackEvent

case class StarRemoved(user: String,
    item: Json, // TODO: Different item types -- https://api.slack.com/methods/stars.list
    event_ts: String)
  extends SlackEvent

case class Subteam(
    id: String,
    team_id: String,
    is_usergroup: Boolean,
    name: String,
    description: String,
    handle: String,
    is_external: Boolean,
    date_create: Long,
    date_update: Long,
    date_delete: Long,
    created_by: String,
    updated_by: String,
    prefs: Json,
)
object Subteam { implicit val codec = deriveCodec[Subteam] }

case class SubteamCreated(subteam: Subteam) extends SlackEvent

case class SubteamMembersChanged(
    subteam_id: String,
    team_id: String,
    date_previous_update: Long,
    date_update: Long,
    added_users: List[String],
    added_users_count: Int,
    removed_users: List[String],
    removed_users_count: Int,
) extends SlackEvent

case class SubteamSelfAdded(subteam_id: String) extends SlackEvent

case class SubteamSelfRemoved(subteam_id: String) extends SlackEvent

case class SubteamUpdated(subteam: Json) extends SlackEvent

case class TeamAccessGranted(team_ids: List[String]) extends SlackEvent

case class TeamAccessRevoked(team_ids: List[String]) extends SlackEvent

case class TeamDomainChange(url: String, domain: String) extends SlackEvent

case class TeamJoin(user: User) extends SlackEvent

case class TeamRename(name: String) extends SlackEvent

case class Tokens(oauth: List[String], bot: List[String])
object Tokens { implicit val codec = deriveCodec[Tokens] }

case class TokensRevoked(tokens: Tokens) extends SlackEvent

case class UserChange(user: User) extends SlackEvent

object SlackEvent {
  // Non-event types
  implicit val teamFmt                  = deriveCodec[Team]
  implicit val inviteRequestFmt         = deriveCodec[InviteRequest]
  implicit val linkFmt                  = deriveCodec[Link]
  implicit val dndStatusFmt             = deriveCodec[DndStatus]

  // Message Formats
  implicit val messageFmt               = deriveCodec[Message]
  implicit val botMessageFmt            = deriveCodec[BotMessage]
  implicit val editMessageFmt           = deriveCodec[EditMessage]
  implicit val replyMarkerFmt           = deriveCodec[ReplyMarker]
  implicit val replyMessageFmt          = deriveCodec[ReplyMessage]
  implicit val messageChangedFmt        = deriveCodec[MessageChanged]
  implicit val messageDeletedFmt        = deriveCodec[MessageDeleted]
  implicit val messageRepliedFmt        = deriveCodec[MessageReplied]

  // Event Formats
  implicit val appHomeOpenedFmt         = deriveCodec[AppHomeOpened]
  implicit val appMentionFmt            = deriveCodec[AppMention]
  implicit val appRequestedFmt          = deriveCodec[AppRequested]
  implicit val appUninstalledFmt        = deriveCodec[AppUninstalled]
  implicit val callRejectedFmt          = deriveCodec[CallRejected]
  implicit val channelArchiveFmt        = deriveCodec[ChannelArchive]
  implicit val channelCreatedFmt        = deriveCodec[ChannelCreated]
  implicit val channelDeletedFmt        = deriveCodec[ChannelDeleted]
  implicit val channelHistoryChangedFmt = deriveCodec[ChannelHistoryChanged]
  implicit val channelLeftFmt           = deriveCodec[ChannelLeft]
  implicit val channelRenameFmt         = deriveCodec[ChannelRename]
  implicit val channelSharedFmt         = deriveCodec[ChannelShared]
  implicit val channelUnarchiveFmt      = deriveCodec[ChannelUnarchive]
  implicit val channelUnsharedFmt       = deriveCodec[ChannelUnshared]
  implicit val dndUpdatedFmt            = deriveCodec[DndUpdated]
  implicit val dndUpdatedUserFmt        = deriveCodec[DndUpdatedUser]
  implicit val emailDomainChangedFmt    = deriveCodec[EmailDomainChanged]
  implicit val emojiChangedFmt          = deriveCodec[EmojiChanged]
  implicit val fileChangeFmt            = deriveCodec[FileChange]
  implicit val fileCommentAddedFmt      = deriveCodec[FileCommentAdded]
  implicit val fileCommentDeletedFmt    = deriveCodec[FileCommentDeleted]
  implicit val fileCommentEditedFmt     = deriveCodec[FileCommentEdited]
  implicit val fileCreatedFmt           = deriveCodec[FileCreated]
  implicit val fileDeletedFmt           = deriveCodec[FileDeleted]
  implicit val filePublicFmt            = deriveCodec[FilePublic]
  implicit val filePrivateFmt           = deriveCodec[FilePrivate]
  implicit val fileSharedFmt            = deriveCodec[FileShared]
  implicit val fileUnsharedFmt          = deriveCodec[FileUnshared]
  implicit val gridMigrationFinishedFmt = deriveCodec[GridMigrationFinished]
  implicit val gridMigrationStartedFmt  = deriveCodec[GridMigrationStarted]
  implicit val groupArchiveFmt          = deriveCodec[GroupArchive]
  implicit val groupCloseFmt            = deriveCodec[GroupClose]
  implicit val groupDeletedFmt          = deriveCodec[GroupDeleted]
  implicit val groupHistoryChangedFmt   = deriveCodec[GroupHistoryChanged]
  implicit val groupLeftFmt             = deriveCodec[GroupLeft]
  implicit val groupOpenFmt             = deriveCodec[GroupOpen]
  implicit val groupRenameFmt           = deriveCodec[GroupRename]
  implicit val groupUnarchiveFmt        = deriveCodec[GroupUnarchive]
  implicit val imCloseFmt               = deriveCodec[ImClose]
  implicit val imCreatedFmt             = deriveCodec[ImCreated]
  implicit val imHistoryChangedFmt      = deriveCodec[ImHistoryChanged]
  implicit val imOpenFmt                = deriveCodec[ImOpen]
  implicit val inviteRequestedFmt       = deriveCodec[InviteRequested]
  implicit val linkSharedFmt            = deriveCodec[LinkShared]
  implicit val memberJoinedChannelFmt   = deriveCodec[MemberJoinedChannel]
  implicit val memberLeftChannelFmt     = deriveCodec[MemberLeftChannel]
  implicit val pinAddedFmt              = deriveCodec[PinAdded]
  implicit val pinRemovedFmt            = deriveCodec[PinRemoved]
  implicit val reactionAddedFmt         = deriveCodec[ReactionAdded]
  implicit val reactionRemovedFmt       = deriveCodec[ReactionRemoved]
  implicit val starAddedFmt             = deriveCodec[StarAdded]
  implicit val starRemovedFmt           = deriveCodec[StarRemoved]
  implicit val subteamCreatedFmt        = deriveCodec[SubteamCreated]
  implicit val subteamMembersChangedFmt = deriveCodec[SubteamMembersChanged]
  implicit val subteamSelfAddedFmt      = deriveCodec[SubteamSelfAdded]
  implicit val subteamSelfRemovedFmt    = deriveCodec[SubteamSelfRemoved]
  implicit val subteamUpdatedFmt        = deriveCodec[SubteamUpdated]
  implicit val teamAccessGrantedFmt     = deriveCodec[TeamAccessGranted]
  implicit val teamAccessRevokedFmt     = deriveCodec[TeamAccessRevoked]
  implicit val teamDomainChangeFmt      = deriveCodec[TeamDomainChange]
  implicit val teamJoinFmt              = deriveCodec[TeamJoin]
  implicit val teamRenameFmt            = deriveCodec[TeamRename]
  implicit val tokensRevokedFmt         = deriveCodec[TokensRevoked]
  implicit val userChangeFmt            = deriveCodec[UserChange]

  // Message sub-types
  import MessageSubtypes._

  implicit val messageSubtypeMeMessageFmt          = deriveCodec[MeMessage]
  implicit val messageSubtypeChannelNameMessageFmt = deriveCodec[ChannelNameMessage]
  implicit val messageSubtypeFileShareMessageFmt   = deriveCodec[FileShareMessage]
  implicit val messageSubtypeHandledSubtypeFmt     = deriveCodec[UnhandledSubtype]

  implicit val messageWithSubtypeWrites: Encoder[MessageWithSubtype] = Encoder.forProduct6(
    "ts",
    "channel",
    "user",
    "text",
    "is_starred",
    "subtype"
  )((msg: MessageWithSubtype) => (msg.ts, msg.channel, msg.user, msg.text, msg.is_starred, msg.messageSubType.subtype))

  // Event Reads/Writes
  implicit val slackEventWrites = Encoder.instance[SlackEvent] {
    case e: Message                 => e.asJson
    case e: MessageChanged          => e.asJson
    case e: MessageDeleted          => e.asJson
    case e: MessageReplied          => e.asJson
    case e: BotMessage              => e.asJson
    case e: MessageWithSubtype      => e.asJson

    case e: AppHomeOpened           => e.asJson
    case e: AppMention              => e.asJson
    case e: AppRequested            => e.asJson
    case e: AppUninstalled          => e.asJson
    case e: CallRejected            => e.asJson
    case e: ChannelArchive          => e.asJson
    case e: ChannelCreated          => e.asJson
    case e: ChannelDeleted          => e.asJson
    case e: ChannelHistoryChanged   => e.asJson
    case e: ChannelLeft             => e.asJson
    case e: ChannelRename           => e.asJson
    case e: ChannelShared           => e.asJson
    case e: ChannelUnarchive        => e.asJson
    case e: ChannelUnshared         => e.asJson
    case e: DndUpdated              => e.asJson
    case e: DndUpdatedUser          => e.asJson
    case e: EmailDomainChanged      => e.asJson
    case e: EmojiChanged            => e.asJson
    case e: FileChange              => e.asJson
    case e: FileCommentAdded        => e.asJson
    case e: FileCommentDeleted      => e.asJson
    case e: FileCommentEdited       => e.asJson
    case e: FileCreated             => e.asJson
    case e: FileDeleted             => e.asJson
    case e: FilePublic              => e.asJson
    case e: FilePrivate             => e.asJson
    case e: FileShared              => e.asJson
    case e: FileUnshared            => e.asJson
    case e: GridMigrationFinished   => e.asJson
    case e: GridMigrationStarted    => e.asJson
    case e: GroupArchive            => e.asJson
    case e: GroupClose              => e.asJson
    case e: GroupDeleted            => e.asJson
    case e: GroupHistoryChanged     => e.asJson
    case e: GroupLeft               => e.asJson
    case e: GroupOpen               => e.asJson
    case e: GroupRename             => e.asJson
    case e: GroupUnarchive          => e.asJson
    case e: ImClose                 => e.asJson
    case e: ImCreated               => e.asJson
    case e: ImHistoryChanged        => e.asJson
    case e: ImOpen                  => e.asJson
    case e: InviteRequested         => e.asJson
    case e: LinkShared              => e.asJson
    case e: MemberJoinedChannel     => e.asJson
    case e: MemberLeftChannel       => e.asJson
    case e: PinAdded                => e.asJson
    case e: PinRemoved              => e.asJson
    case e: ReactionAdded           => e.asJson
    case e: ReactionRemoved         => e.asJson
    case e: StarAdded               => e.asJson
    case e: StarRemoved             => e.asJson
    case e: SubteamCreated          => e.asJson
    case e: SubteamMembersChanged   => e.asJson
    case e: SubteamSelfAdded        => e.asJson
    case e: SubteamSelfRemoved      => e.asJson
    case e: SubteamUpdated          => e.asJson
    case e: TeamAccessGranted       => e.asJson
    case e: TeamAccessRevoked       => e.asJson
    case e: TeamDomainChange        => e.asJson
    case e: TeamJoin                => e.asJson
    case e: TeamRename              => e.asJson
    case e: TokensRevoked           => e.asJson
    case e: UserChange              => e.asJson
  }

  implicit val subMessageReads = new Decoder[MessageWithSubtype] {

    override def apply(c: HCursor): Result[MessageWithSubtype] =
      for {
        subtype <- c.downField("subtype").as[String]
        result <- subtype match {
          case "me_message"   => c.as[MeMessage]
          case "channel_name" => c.as[ChannelNameMessage]
          case "file_share"   => c.as[FileShareMessage]
          case _              => c.as[UnhandledSubtype]
        }
        ts        <- c.downField("ts").as[String]
        channel   <- c.downField("channel").as[String]
        user      <- c.downField("user").as[String]
        text      <- c.downField("text").as[String]
        isStarred <- c.downField("is_starred").as[Option[Boolean]]
      } yield MessageWithSubtype(ts, channel, user, text, isStarred, result)
  }

  implicit val slackEventReads = new Decoder[SlackEvent] {

    override def apply(c: HCursor): Result[SlackEvent] = {
      val event: Either[DecodingFailure, SlackEvent] = for {
        etype   <- c.downField("type").as[String]
        subtype <- c.downField("subtype").as[Option[String]]
        result <- etype match {
          case "message" if subtype.contains("bot_message")     => c.as[BotMessage]
          case "message" if subtype.contains("message_changed") => c.as[MessageChanged]
          case "message" if subtype.contains("message_deleted") => c.as[MessageDeleted]
          case "message" if subtype.contains("message_replied") => c.as[MessageReplied]
          case "message" if subtype.isDefined                   => c.as[MessageWithSubtype]
          case "message"                                        => c.as[Message]

          case "app_home_opened"                                => c.as[AppHomeOpened]
          case "app_mention"                                    => c.as[AppMention]
          case "app_requested"                                  => c.as[AppRequested]
          case "call_rejected"                                  => c.as[CallRejected]
          case "channel_archive"                                => c.as[ChannelArchive]
          case "channel_created"                                => c.as[ChannelCreated]
          case "channel_deleted"                                => c.as[ChannelDeleted]
          case "channel_history_changed"                        => c.as[ChannelHistoryChanged]
          case "channel_left"                                   => c.as[ChannelLeft]
          case "channel_rename"                                 => c.as[ChannelRename]
          case "channel_shared"                                 => c.as[ChannelShared]
          case "channel_unarchive"                              => c.as[ChannelUnarchive]
          case "channel_unshared"                               => c.as[ChannelUnshared]
          case "dnd_updated"                                    => c.as[DndUpdated]
          case "dnd_updated_user"                               => c.as[DndUpdatedUser]
          case "email_domain_changed"                           => c.as[EmailDomainChanged]
          case "emoji_changed"                                  => c.as[EmojiChanged]
          case "file_change"                                    => c.as[FileChange]
          case "file_comment_added"                             => c.as[FileCommentAdded]
          case "file_comment_deleted"                           => c.as[FileCommentDeleted]
          case "file_comment_edited"                            => c.as[FileCommentEdited]
          case "file_created"                                   => c.as[FileCreated]
          case "file_deleted"                                   => c.as[FileDeleted]
          case "file_public"                                    => c.as[FilePublic]
          case "file_private"                                   => c.as[FilePrivate]
          case "file_shared"                                    => c.as[FileShared]
          case "file_unshared"                                  => c.as[FileUnshared]
          case "grid_migration_finished"                        => c.as[GridMigrationFinished]
          case "grid_migration_started"                         => c.as[GridMigrationStarted]
          case "group_archive"                                  => c.as[GroupArchive]
          case "group_close"                                    => c.as[GroupClose]
          case "group_deleted"                                  => c.as[GroupDeleted]
          case "group_history_changed"                          => c.as[GroupHistoryChanged]
          case "group_left"                                     => c.as[GroupLeft]
          case "group_open"                                     => c.as[GroupOpen]
          case "group_rename"                                   => c.as[GroupRename]
          case "group_unarchive"                                => c.as[GroupUnarchive]
          case "im_close"                                       => c.as[ImClose]
          case "im_created"                                     => c.as[ImCreated]
          case "im_history_changed"                             => c.as[ImHistoryChanged]
          case "im_open"                                        => c.as[ImOpen]
          case "invite_requested"                               => c.as[InviteRequested]
          case "link_shared"                                    => c.as[LinkShared]
          case "member_joined_channel"                          => c.as[MemberJoinedChannel]
          case "member_left_channel"                            => c.as[MemberLeftChannel]
          case "pin_added"                                      => c.as[PinAdded]
          case "pin_removed"                                    => c.as[PinRemoved]
          case "reaction_added"                                 => c.as[ReactionAdded]
          case "reaction_removed"                               => c.as[ReactionRemoved]
          case "star_added"                                     => c.as[StarAdded]
          case "star_removed"                                   => c.as[StarRemoved]
          case "subteam_created"                                => c.as[SubteamCreated]
          case "subteam_members_changed"                        => c.as[SubteamMembersChanged]
          case "subteam_self_added"                             => c.as[SubteamSelfAdded]
          case "subteam_self_removed"                           => c.as[SubteamSelfRemoved]
          case "subteam_updated"                                => c.as[SubteamUpdated]
          case "team_access_granted"                            => c.as[TeamAccessGranted]
          case "team_access_revoked"                            => c.as[TeamAccessRevoked]
          case "team_domain_change"                             => c.as[TeamDomainChange]
          case "team_join"                                      => c.as[TeamJoin]
          case "team_rename"                                    => c.as[TeamRename]
          case "tokens_revoked"                                 => c.as[TokensRevoked]
          case "user_change"                                    => c.as[UserChange]
          case t: String                                        => Left(DecodingFailure(s"Invalid type property: $t", List.empty))
        }
      } yield result

      event
    }
  }
}
