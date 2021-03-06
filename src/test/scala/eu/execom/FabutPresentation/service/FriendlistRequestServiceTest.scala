package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.AppTestConfiguration._
import org.junit.Assert._
import eu.execom.FabutPresentation.persistence.FriendRequestStatus
import eu.execom.FabutPresentation.persistence.Friendlist
import eu.execom.FabutPresentation.persistence.FriendlistRequest
import eu.execom.FabutPresentation.persistence.User
import eu.execom.FabutPresentation.util.BadRequestException

class FriendlistRequestPersistenceTest extends CoreTest {

  @Test
  def friendlistRequestWithNoPendingRequest = {
    //  setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)
    //    method

    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIDRequesteeID(user1.id, user2.id).get
    val result2 = friendlistRequestDao.findByRequesterIDRequesteeID(user2.id, user1.id).get

    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.USER1ID, user1.id),
      value(FriendlistRequest.USER2ID, user2.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.SENT))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.USER1ID, user2.id),
      value(FriendlistRequest.USER2ID, user1.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.PENDING))

  }

  @Test
  def friendlistRequestWithPendingRequest = {
    //  setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)
    friendlistRequestDao.save(new FriendlistRequest(user1.id, user2.id, FriendRequestStatus.SENT))
    friendlistRequestDao.save(new FriendlistRequest(user2.id, user1.id, FriendRequestStatus.PENDING))

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user2.id, user1.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIDRequesteeID(user1.id, user2.id).get
    val result2 = friendlistRequestDao.findByRequesterIDRequesteeID(user2.id, user1.id).get
    val friendlist = friendlistDao.findAll

    assertEquals(friendlist.size, 2)

    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.USER1ID, user1.id),
      value(FriendlistRequest.USER2ID, user2.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.CONNECTED))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.USER1ID, user2.id),
      value(FriendlistRequest.USER2ID, user1.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.CONNECTED))

    assertObject(friendlist(0),
      notNull(Friendlist.ID),
      value(Friendlist.USER1ID, user2.id),
      value(Friendlist.USER2ID, user1.id),
      notNull(Friendlist.CONNECTIONDATE))

    assertObject(friendlist(1),
      notNull(Friendlist.ID),
      value(Friendlist.USER1ID, user1.id),
      value(Friendlist.USER2ID, user2.id),
      notNull(Friendlist.CONNECTIONDATE))

    assertEntityWithSnapshot(user1, value(User.NUMBEROFFRIENDS, user1.numberOfFriends))
    assertEntityWithSnapshot(user2, value(User.NUMBEROFFRIENDS, user2.numberOfFriends))
  }

  @Test(expected = classOf[BadRequestException])
  def friendlistRequestWithRequestAlreadySent = {

    //    setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

  }

  @Test(expected = classOf[BadRequestException])
  def friendlistRequestWithUsersAlreadyConnected = {

    //    setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com", 0)
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com", 0)
    userDao.save(user1)
    userDao.save(user2)
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)
    friendlistRequestService.sendFriendlistRequest(user2.id, user1.id)

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

  }

}