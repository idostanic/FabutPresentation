package eu.execom.FabutPresentation.persistence

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.service.FriendlistRequestService
import eu.execom.FabutPresentation.AppTestConfiguration._

class FriendlistRequestPersistenceTest extends CoreTest {

  @Test
  def friendlistRequestWithNoPendingRequest = {
    //  setup
    val requester = new User("pera", Some("peric"), "pera@peric.com")
    val requestee = new User("mika", Some("mikic"), "mika@mikic.com")

    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(requester.id, requestee.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIDRequesteeID(requester.id, requestee.id)
    val result2 = friendlistRequestDao.findByRequesterIDRequesteeID(requestee.id, requester.id)
    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requester.id),
      value(FriendlistRequest.REQUESTEEID, requestee.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.PENDING))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requestee.id),
      value(FriendlistRequest.REQUESTEEID, requester.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.PENDING))

  }

  @Test
  def friendlistRequestWithPendingRequest = {
    //  setup
    val requester = new User("pera", Some("peric"), "pera@peric.com")
    val requestee = new User("mika", Some("mikic"), "mika@mikic.com")
    friendlistRequestDao.save(new FriendlistRequest(requester.id, requestee.id, FriendRequestStatus.PENDING))
    friendlistRequestDao.save(new FriendlistRequest(requestee.id, requester.id, FriendRequestStatus.PENDING))
    //    method
    takeSnapshot()
    friendlistRequestService.sendFriendlistRequest(requester.id, requestee.id)

    //    assert
    val result1 = friendlistRequestDao.findByRequesterIDRequesteeID(requester.id, requestee.id)
    val result2 = friendlistRequestDao.findByRequesterIDRequesteeID(requestee.id, requester.id)

    assertObject(result1,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requester.id),
      value(FriendlistRequest.REQUESTEEID, requestee.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.BEFRENDED))

    assertObject(result2,
      notNull(FriendlistRequest.ID),
      value(FriendlistRequest.REQUESTERID, requestee.id),
      value(FriendlistRequest.REQUESTEEID, requester.id),
      value(FriendlistRequest.STATUS, FriendRequestStatus.BEFRENDED))

  }

}