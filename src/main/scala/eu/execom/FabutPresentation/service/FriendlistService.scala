package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

class FriendlistService(val friendlistDao: FriendlistDao) extends Logging {

  def save(friendlist: Friendlist)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(friendlist: $friendlist)")

    friendlistDao.save(friendlist)
  }

  def update(friendlist: Friendlist)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(friendlist: $friendlist)")

    friendlistDao.update(friendlist)
  }

  def delete(friendlist: Friendlist)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(friendlist: $friendlist)")

    friendlistDao.deleteById(friendlist.id)
  }

}
