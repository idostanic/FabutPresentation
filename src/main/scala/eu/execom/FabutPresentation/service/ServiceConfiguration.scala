package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence.SlickPersistenceConfiguration
import eu.execom.FabutPresentation.util._

trait ServiceConfiguration extends SlickPersistenceConfiguration {

  //services
  lazy val userService: UserService = new UserService(userDao)
  lazy val invitationService: InvitationService = new InvitationService(invitationDao)
  lazy val friendlistService: FriendlistService = new FriendlistService(friendlistDao)
  lazy val friendlistRequestService: FriendlistRequestService = new FriendlistRequestService(friendlistRequestDao, friendlistDao, userDao)
  lazy val statusService: StatusService = new StatusService(statusDao)
}
