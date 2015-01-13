package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence.SlickPersistenceConfiguration
import eu.execom.FabutPresentation.util._

trait ServiceConfiguration extends SlickPersistenceConfiguration {

  def smtpUrl: String
  def smtpPort: Int
  def smtpUserName: String
  def smtpPassword: String
  def smtpSslOnConnect: Boolean
  def feedbackEmail: String
  def noFacebookEmail: String
  lazy val mailSender = new MailSender(noFacebookEmail, feedbackEmail, smtpUrl, smtpPort, smtpUserName, smtpPassword, smtpSslOnConnect)

  //services
  lazy val userService: UserService = new UserService(userDao, invitationDao, friendlistRequestService)
  lazy val invitationService: InvitationService = new InvitationService(invitationDao, mailService)
  lazy val friendlistService: FriendlistService = new FriendlistService(friendlistDao)
  lazy val friendlistRequestService: FriendlistRequestService = new FriendlistRequestService(friendlistRequestDao, friendlistDao, userDao)
  lazy val statusService: StatusService = new StatusService(statusDao)
  lazy val mailService: MailSender = new MailSender(noFacebookEmail, feedbackEmail, smtpUrl, smtpPort, smtpUserName, smtpPassword, smtpSslOnConnect)
}
