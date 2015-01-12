import eu.execom.dry.generator.Project

object FabutPresentationModel extends Project("eu.execom", "FabutPresentation") {

  val idostanic = developer("idostanic")
  val bgvoka = developer("bgvoka")

  //  enumerations

  val invitationStatusEnum = enumeration("InvitationStatus")
  val PENDING_INVITATION = invitationStatusEnum.value("PENDING")
  val USED_INVITATION = invitationStatusEnum.value("USED")

  val friendlistRequestStatusEnum = enumeration("FriendRequestStatus")
  val PENDING_REQUEST = friendlistRequestStatusEnum.value("PENDING")
  val SENT_REQUEST = friendlistRequestStatusEnum.value("SENT")
  val CONNECTED = friendlistRequestStatusEnum.value("CONNECTED")

  //  models

  val user = securedSqlModel("User")
  val userId = user.int("id").primaryKey
  val userFirstName = user.text("firstName")
  val userLastName = user.text("lastName").optional
  val userEmail = user.text("email").unique.maxSize(50)

  val invitation = sqlModel("Invitation")
  val invitationId = invitation.int("id").primaryKey
  val invitationFrom = invitation.reference("from", userId)
  val invitationEmail = invitation.text("email")
  val invitationStatus = invitation.enum("status", invitationStatusEnum)

  val friendlist = sqlModel("Friendlist")
  val friendlistId = friendlist.int("id").primaryKey
  val friendlistFriend1Id = friendlist.reference("friend1", userId).indexed
  val friendlistFriend2Id = friendlist.reference("friend2", userId)
  val friendlistConnectionDate = friendlist.date("connectionDate")

  val friendlistRequest = sqlModel("FriendlistRequest")
  val friendlistRequestId = friendlistRequest.int("id").primaryKey
  val friendlistRequester = friendlistRequest.reference("requester", userId)
  val friendlistRequestee = friendlistRequest.reference("requestee", userId)
  val friendlistRequestStatus = friendlistRequest.enum("status", friendlistRequestStatusEnum)
  friendlistRequest.addUnique(friendlistRequester, friendlistRequestee)

  val status = sqlModel("Status")
  val statusId = status.int("id").primaryKey
  val statusContent = status.text("content")
  val statusUserId = status.reference("fromId", userId)
  val statusDate = status.date("creationDate")

  //  DAOs
  val friendlistRequestByRequesterIdRequesteeId = friendlistRequest.dao.findMethod(
    friendlistRequest.query.
      equals("requesterID", friendlistRequester).
      equals("requesteeID", friendlistRequestee))

}
