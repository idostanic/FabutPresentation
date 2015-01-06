package eu.execom.FabutPresentation.persistance

import eu.execom.FabutPresentation.CoreTest
import org.junit.Test
import eu.execom.FabutPresentation.AppTestConfiguration._
import eu.execom.FabutPresentation.persistence._

class UserPersistanceTest extends CoreTest {

  @Test
  def saveUserTest() = {

    //    setup
    val user1 = new User("pera", "peric", "pera@peric.com")
    userDao.save(user1)
    //    method
    takeSnapshot()
    val result = userDao.findAll

    //    assert
    assertObject(result(0),
      notNull(User.ID),
      value(User.FIRSTNAME, "pera"),
      value(User.LASTNAME, "peric"),
      value(User.EMAIL, "pera@peric.com"))
  }

}