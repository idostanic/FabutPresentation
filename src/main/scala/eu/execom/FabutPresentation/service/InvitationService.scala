package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

class InvitationService(val invitationDao: InvitationDao) extends Logging {

  def save(invitation: Invitation)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(invitation: $invitation)")

    invitationDao.save(invitation)
  }

  def update(invitation: Invitation)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(invitation: $invitation)")

    invitationDao.update(invitation)
  }

  def delete(invitation: Invitation)(implicit session: SlickSession): Unit = {
    logger.trace(s".delete(invitation: $invitation)")

    invitationDao.deleteById(invitation.id)
  }

}
