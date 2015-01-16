package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence.SlickPersistenceConfiguration

trait ServiceConfiguration extends SlickPersistenceConfiguration {

  lazy val passwordEncoder = new PasswordEncoder

  def appEmail: String
  def appName: String
  def appUrl: String
  lazy val securedService: SecuredService = new SecuredService(userDao, userSessionDao, passwordEncoder, appEmail, appName, appUrl)

  //services
  lazy val userService: UserService = new UserService(userDao)
  lazy val userSessionService: UserSessionService = new UserSessionService(userSessionDao)
  lazy val invitationService: InvitationService = new InvitationService(invitationDao)
  lazy val friendlistService: FriendlistService = new FriendlistService(friendlistDao)
  lazy val friendlistRequestService: FriendlistRequestService = new FriendlistRequestService(friendlistRequestDao)
  lazy val statusService: StatusService = new StatusService(statusDao)
}
