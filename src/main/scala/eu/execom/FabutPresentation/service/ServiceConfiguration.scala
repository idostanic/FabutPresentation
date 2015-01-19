package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence.SlickPersistenceConfiguration
import eu.execom.FabutPresentation.util._


trait ServiceConfiguration extends SlickPersistenceConfiguration {

  def smtpUrl: String
  def smtpPort: Int
  def smtpUserName: String
  def smtpPassword: String
  def smtpSslOnConnect: Boolean
  def appName: String
  def appEmail:String
  def appUrl:String

  //services
  lazy val  passwordEncoder= new PasswordEncoder
  lazy val mailService = new MailSender( smtpUrl, smtpPort, smtpUserName, smtpPassword, smtpSslOnConnect)
  lazy val userService: UserService = new UserService(userDao, invitationDao,  friendlistDao, friendlistRequestDao,friendlistRequestService)
  lazy val invitationService: InvitationService = new InvitationService(invitationDao, mailService)
  lazy val friendlistService: FriendlistService = new FriendlistService(friendlistDao)
  lazy val friendlistRequestService: FriendlistRequestService = new FriendlistRequestService(friendlistRequestDao, friendlistDao, userDao)
  lazy val statusService: StatusService = new StatusService(statusDao)
  lazy val securedService: SecuredService= new SecuredService( userDao,  userSessionDao,  passwordEncoder, appEmail, appName,appUrl)

}
