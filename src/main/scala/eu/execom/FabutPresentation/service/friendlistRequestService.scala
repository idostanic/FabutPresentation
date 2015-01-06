package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

class friendlistRequestService(val friendlistRequestDao: friendlistRequestDao) extends Logging {

  def save(friendlistRequest: friendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.save(friendlistRequest)
  }

  def update(friendlistRequest: friendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.update(friendlistRequest)
  }

  def delete(friendlistRequest: friendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(friendlistRequest: $friendlistRequest)")

    friendlistRequestDao.deleteById(friendlistRequest.id)
  }

}
