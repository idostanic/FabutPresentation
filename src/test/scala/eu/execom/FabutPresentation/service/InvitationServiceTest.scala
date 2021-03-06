package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.AppTestConfiguration._
import org.junit.Assert._
import eu.execom.FabutPresentation.persistence.FriendRequestStatus
import eu.execom.FabutPresentation.persistence.Friendlist
import eu.execom.FabutPresentation.persistence.FriendlistRequest
import eu.execom.FabutPresentation.persistence.User
import eu.execom.FabutPresentation.persistence.Invitation
import eu.execom.FabutPresentation.persistence.InvitationStatus
import eu.execom.fabut.pair.AssertMail


class InvitationServiceTest extends CoreTest {

  @Test
  def sendInvitationWithNoPendingInvitation {
    //    setup
    val user = userService.createUser(new User("pera", Some("peric"), "pera@peric.com", 0))
    //    method
    takeSnapshot()
    invitationService.inviteUser(user, "mika@mikic.com")

    //    assert 
    val invitation = invitationDao.findAll

    assertObject(invitation(0),
      notNull(Invitation.ID),
      value(Invitation.EMAIL, "mika@mikic.com"),
      value(Invitation.FROMID, user.id),
      value(Invitation.STATUS, InvitationStatus.PENDING))

    assertMail("mika@mikic.com", "FABUT")
  }
}