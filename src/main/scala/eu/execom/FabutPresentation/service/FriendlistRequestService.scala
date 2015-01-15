package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

class FriendlistRequestService(val friendlistRequestDao: FriendlistRequestDao) extends Logging {

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

}
