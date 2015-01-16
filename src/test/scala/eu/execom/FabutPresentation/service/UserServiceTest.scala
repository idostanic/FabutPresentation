package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.persistence.User
import eu.execom.FabutPresentation.AppTestConfiguration._

class UserServiceTest extends CoreTest {

  @Test
  def testDeleteUserAccountUserSendsInvitation(): Unit = {
    //    setup
    val user = new User("pera", Some("peric"), "pera@peric.com", 0)
    userDao.save(user)

    invitationService.inviteUser(user, "mika@mikic.com")

    val invitation = invitationDao.findByFromId(user.id)

    //    method
    takeSnapshot()
    userService.deleteUserAccount(user)

    assertEntityAsDeleted(user)
    assertEntityAsDeleted(invitation(0))

    assertMail("mika@mikic.com", "FABUT")
  }

  @Test
  def testDeleteUserAccountUserSentFriendlistRequestToAnother(): Unit = {
    //  setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)

    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

    val friendlistRequest1 = friendlistRequestDao.findByUser1Id(user1.id)
    val friendlistRequest2 = friendlistRequestDao.findByUser2Id(user1.id)

    //method
    takeSnapshot()
    userService.deleteUserAccount(user1)

    assertEntityAsDeleted(user1)
    assertEntityAsDeleted(friendlistRequest1(0))
    assertEntityAsDeleted(friendlistRequest2(0))
  }

  @Test
  def testDeleteUserAccountAlreadyFriends(): Unit = {

    //  setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)

    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)
    friendlistRequestService.sendFriendlistRequest(user2.id, user1.id)

    val friendlist1 = friendlistDao.findByUser1Id(user1.id)
    val friendlist2 = friendlistDao.findByUser2Id(user1.id)

    val friendlistRequest1 = friendlistRequestDao.findByUser1Id(user1.id)
    val friendlistRequest2 = friendlistRequestDao.findByUser2Id(user1.id)

    //method
    takeSnapshot()
    userService.deleteUserAccount(user1)

    assertEntityAsDeleted(user1)
    assertEntityAsDeleted(friendlist1(0))
    assertEntityAsDeleted(friendlist2(0))
    assertEntityAsDeleted(friendlistRequest1(0))
    assertEntityAsDeleted(friendlistRequest2(0))
  }
}