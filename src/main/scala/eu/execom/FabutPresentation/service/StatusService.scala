package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

class StatusService(val statusDao: StatusDao) extends Logging {

  def save(status: Status)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(status: $status)")

    statusDao.save(status)
  }

  def update(status: Status)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(status: $status)")

    statusDao.update(status)
  }

  def delete(status: Status)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(status: $status)")

    statusDao.deleteById(status.id)
  }

}
