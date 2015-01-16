package eu.execom.FabutPresentation

import java.util.UUID

import eu.execom.FabutPresentation.AppTestConfiguration._
import eu.execom.FabutPresentation.persistence._
import eu.execom.fabut._
import org.joda.time.DateTime
import org.junit.{After, Before}
import org.junit.Assert._

import scala.reflect.runtime.universe.{Type, typeOf}
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

abstract class CoreTest extends FabutRepository {

  implicit var slickSession: SlickSession = null
  
  @Before
  override def before() = {

    //opening test transaction
    slickSession = slickDb.createSession()
    slickSession.conn.setAutoCommit(false)
    beforeTest()

  }

  @After
  override def after() = {

    try {
      afterTest()
    } finally {

      // simulating transaction
      slickSession.rollback()
      slickSession.close()
    }
  }

  override def entityTypes(): List[Type] = typeOf[User] :: typeOf[UserSession] :: typeOf[Invitation] :: typeOf[Friendlist] :: typeOf[FriendlistRequest] :: typeOf[Status] :: Nil
  override def complexTypes(): List[Type] = Nil
  override def ignoredTypes(): List[Type] = Nil


  override def findById(entityClass: Type, id: Any): Any =
    if (entityClass == typeOf[User]) userDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[UserSession]) userSessionDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Invitation]) invitationDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Friendlist]) friendlistDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[FriendlistRequest]) friendlistRequestDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Status]) statusDao.findById(id.asInstanceOf[Int]).orNull
    else throw new IllegalStateException("Unsupported entity class " + entityClass)


  override def findAll(entityClass: Type): List[_] =
    if (entityClass == typeOf[User]) userDao.findAll
    else if (entityClass == typeOf[UserSession]) userSessionDao.findAll
    else if (entityClass == typeOf[Invitation]) invitationDao.findAll
    else if (entityClass == typeOf[Friendlist]) friendlistDao.findAll
    else if (entityClass == typeOf[FriendlistRequest]) friendlistRequestDao.findAll
    else if (entityClass == typeOf[Status]) statusDao.findAll
    else throw new IllegalStateException("Unsupported entity class " + entityClass)


  override def customAssertEquals(expected: Any, actual: Any) = (expected, actual) match {
    case (e: DateTime, a: DateTime) => assertTrue(Math.abs(e.getMillis - a.getMillis) < 1000)
    case (e: Array[Byte], a: Array[Byte]) => assertArrayEquals(a,e)
    case (Some(e), Some(a)) => customAssertEquals(e, a)
    case _ => assertEquals(expected, actual)
  }

}
