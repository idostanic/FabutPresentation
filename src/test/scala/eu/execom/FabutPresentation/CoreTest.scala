package eu.execom.FabutPresentation

import java.sql.{ Blob, Date, Timestamp }
import java.util.UUID
import eu.execom.FabutPresentation.AppTestConfiguration._
import eu.execom.FabutPresentation.persistence._
import eu.execom.fabut._
import junit.framework.AssertionFailedError
import org.joda.time.DateTime
import org.junit.Assert._
import org.junit.Assert
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }
import org.junit.Before
import org.junit.After
import scala.reflect.runtime.universe.{ Type, typeOf }
import org.subethamail.wiser.Wiser

abstract class CoreTest extends Assert with FabutRepository with FabutMail {

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

  override def entityTypes: List[Type] = typeOf[User] :: typeOf[Invitation] :: typeOf[Friendlist] :: typeOf[FriendlistRequest] :: typeOf[Status] :: Nil
  override def complexTypes: List[Type] = Nil
  override def ignoredTypes: List[Type] = Nil

  override def findById(entityClass: Type, id: Any): Any =
    if (entityClass == typeOf[User]) userDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Invitation]) invitationDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Friendlist]) friendlistDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[FriendlistRequest]) friendlistRequestDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == typeOf[Status]) statusDao.findById(id.asInstanceOf[Int]).orNull
    else throw new IllegalStateException("Unsupported entity class " + entityClass)

  override def findAll(entityClass: Type): List[Any] =
    if (entityClass == typeOf[User]) userDao.findAll
    else if (entityClass == typeOf[Invitation]) invitationDao.findAll
    else if (entityClass == typeOf[Friendlist]) friendlistDao.findAll
    else if (entityClass == typeOf[FriendlistRequest]) friendlistRequestDao.findAll
    else if (entityClass == typeOf[Status]) statusDao.findAll
    else throw new IllegalStateException("Unsupported entity class " + entityClass)

  override def customAssertEquals(expected: Any, actual: Any) = (expected, actual) match {
    case (e: Timestamp, a: Timestamp) => assertTrue(Math.abs(e.getTime - a.getTime) < 1000)
    case (e: DateTime, a: DateTime) => assertTrue(Math.abs(e.getMillis - a.getMillis) < 1000)
    case (e: Blob, a: Blob) => customAssertEquals(e.length(), a.length())

    //FIXME (nolah) FABUT errro bypass
    case (e: Blob, a: Array[Byte]) => assertArrayEquals(e.getBytes(1, e.length().toInt), a)
    case (e: Array[Byte], a: Blob) => assertArrayEquals(e, a.getBytes(1, a.length().toInt))
    case (e: String, a: Enum) => assertEquals(e, a.name)
    case (e: Enum, a: String) => assertEquals(e.name, a)
    case (e: DateTime, a: Date) => assertTrue(Math.abs(e.getMillis - a.getTime) < 1000)
    case (e: Date, a: DateTime) => assertTrue(Math.abs(e.getTime - a.getMillis) < 1000)
    case (Some(e), Some(a)) => customAssertEquals(e, a)
    case _ => assertEquals(expected, actual)
  }

}
