package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }

class UserService(val userDao: UserDao, val invitationDao: InvitationDao, val friendlistDao: FriendlistDao, val friendlistRequestDao: FriendlistRequestDao, val friendlistRequestService: FriendlistRequestService) extends Logging {

  def save(user: User)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(user: $user)")

    userDao.save(user)
  }

  def update(user: User)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(user: $user)")

    userDao.update(user)
  }

  def delete(user: User)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(user: $user)")

    userDao.deleteById(user.id)
  }

  def createUser(user: User)(implicit session: SlickSession): User = {

    userDao.save(user)
    val newFriendsList = invitationDao.findByEmail(user.email)
    for (friend <- newFriendsList) {
      friendlistRequestService.sendFriendlistRequest(friend.fromId, user.id)
      friend.status = InvitationStatus.USED
      invitationDao.update(friend)
    }
    user
  }

  def deleteUserAccount(user: User)(implicit session: SlickSession): Unit = {

    val invitation = invitationDao.findByFromId(user.id)
    if (invitation.size > 0) {
      invitationDao.deleteById(invitation.head.id)
    }

    val friendlists = friendlistDao.findByUserIdAllFriendlists(user.id)
    for (friendlist <- friendlists) {
      friendlistDao.deleteById(friendlist.id)
    }

    val friendlistRequests = friendlistRequestDao.findByUserIdAllFriendlistRequests(user.id)
    for (friendlistRequest <- friendlistRequests) {
      friendlistRequestDao.deleteById(friendlistRequest.id)
    }

    delete(user)
  }
}
