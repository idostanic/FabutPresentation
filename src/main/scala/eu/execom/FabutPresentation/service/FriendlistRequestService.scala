package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }
import org.joda.time.DateTime

class FriendlistRequestService(val friendlistRequestDao: FriendlistRequestDao, val friendlistDao: FriendlistDao) extends Logging {

  def save(friendlistRequest: FriendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.save(friendlistRequest)
  }

  def update(friendlistRequest: FriendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.update(friendlistRequest)
  }

  def delete(friendlistRequest: FriendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.deleteById(friendlistRequest.id)
  }

  def sendFriendlistRequest(user1Id: Int, user2Id: Int)(implicit session: SlickSession): Unit = {

    val friendlistRequest = friendlistRequestDao.findByRequesterIdRequesteeId(user1Id, user2Id)

    friendlistRequest match {
      case Some(friendlistRequest) =>
        friendlistRequest.status match {
          case FriendRequestStatus.SENT =>
            throw FRIENDLIST_REQUEST_ALREADY_SENT
            println("already sent")
          case FriendRequestStatus.CONNECTED =>
            throw USERS_ALREADY_CONNECTED
            println("already connected")
          case FriendRequestStatus.PENDING =>
            val friendlistRequestReverse = friendlistRequestDao.findByRequesterIdRequesteeId(user2Id, user1Id).get

            friendlistRequest.status = FriendRequestStatus.CONNECTED
            friendlistRequestReverse.status = FriendRequestStatus.CONNECTED
            friendlistRequestDao.update(friendlistRequest)
            friendlistRequestDao.update(friendlistRequestReverse)

            val currentTime = new DateTime()
            friendlistDao.save(new Friendlist(user1Id, user2Id, currentTime))
            friendlistDao.save(new Friendlist(user2Id, user1Id, currentTime))
        }
      case None =>
        friendlistRequestDao.save(new FriendlistRequest(user1Id, user2Id, FriendRequestStatus.SENT))
        friendlistRequestDao.save(new FriendlistRequest(user2Id, user1Id, FriendRequestStatus.PENDING))
    }

  }
}
