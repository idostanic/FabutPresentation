package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util.Logging
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }
import eu.execom.FabutPresentation.service.MailSender

class InvitationService(val invitationDao: InvitationDao, val mailService: MailSender) extends Logging {

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

  def inviteUser(inviter: User, email: String)(implicit session: SlickSession): Unit = {
    mailService.sendEmail(email, None)
    val invitation = invitationDao.findByUserIdEmailStatus(inviter.id, email, InvitationStatus.PENDING)
    if (invitation.size > 0) {
      invitationDao.update(invitation(0))
    } else {
      invitationDao.save(new Invitation(inviter.id, email, InvitationStatus.PENDING))
    }
  }
}