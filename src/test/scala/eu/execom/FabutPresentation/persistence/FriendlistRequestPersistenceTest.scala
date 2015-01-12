package eu.execom.FabutPresentation.persistence

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.service.FriendlistRequestService
import eu.execom.FabutPresentation.AppTestConfiguration._
import eu.execom.dry.generator.TODAY
import org.junit.Assert._
import eu.execom.FabutPresentation.service.USERS_ALREADY_CONNECTED
import eu.execom.FabutPresentation.service.USERS_ALREADY_CONNECTED
import eu.execom.FabutPresentation.util.BadRequestException
import org.joda.time.DateTime

class FriendlistRequestPersistenceTest extends CoreTest {

  @Test
  def friendlistRequestWithNoPendingRequest = {
    //  setup
    val requester = new User("pera", Some("peric"), "pera@peric.com")
    val requestee = new User("mika", Some("mikic"), "mika@mikic.com")
    userDao.save(requester)
    userDao.save(requestee)
    //    method

    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(requester.id, requestee.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIdRequesteeId(requester.id, requestee.id).get
    val result2 = friendlistRequestDao.findByRequesterIdRequesteeId(requestee.id, requester.id).get

    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requester.id),
      value(FriendlistRequest.REQUESTEEID, requestee.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.SENT))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requestee.id),
      value(FriendlistRequest.REQUESTEEID, requester.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.PENDING))

  }

  @Test
  def friendlistRequestWithPendingRequest = {
    //  setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com")
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com")
    userDao.save(user1)
    userDao.save(user2)
    friendlistRequestDao.save(new FriendlistRequest(user1.id, user2.id, FriendRequestStatus.SENT))
    friendlistRequestDao.save(new FriendlistRequest(user2.id, user1.id, FriendRequestStatus.PENDING))

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user2.id, user1.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIdRequesteeId(user1.id, user2.id).get
    val result2 = friendlistRequestDao.findByRequesterIdRequesteeId(user2.id, user1.id).get
    val friendlist = friendlistDao.findAll

    assertEquals(friendlist.size, 2)

    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, user1.id),
      value(FriendlistRequest.REQUESTEEID, user2.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.CONNECTED))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, user2.id),
      value(FriendlistRequest.REQUESTEEID, user1.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.CONNECTED))

    assertObject(friendlist(0),
      notNull(Friendlist.ID),
      value(Friendlist.FRIEND1ID, user2.id),
      value(Friendlist.FRIEND2ID, user1.id),
      notNull(Friendlist.CONNECTIONDATE))

    assertObject(friendlist(1),
      notNull(Friendlist.ID),
      value(Friendlist.FRIEND1ID, user1.id),
      value(Friendlist.FRIEND2ID, user2.id),
      notNull(Friendlist.CONNECTIONDATE))
  }

  @Test(expected = classOf[BadRequestException])
  def friendlistRequestWithRequestAlreadySent = {

    //    setup
    val user1 = new User("pera", Some("peric"), "pera@peric.com")
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com")
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
    val user1 = new User("pera", Some("peric"), "pera@peric.com")
    val user2 = new User("mika", Some("mikic"), "mika@mikic.com")
    userDao.save(user1)
    userDao.save(user2)
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)
    friendlistRequestService.sendFriendlistRequest(user2.id, user1.id)

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(user1.id, user2.id)

  }

}