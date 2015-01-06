package eu.execom.FabutPresentation

import java.sql.{Blob, Date, Timestamp}
import java.util.UUID

import eu.execom.FabutPresentation.AppTestConfiguration._
import eu.execom.FabutPresentation.persistence._
import eu.execom.fabut._
import junit.framework.AssertionFailedError
import org.joda.time.DateTime
import org.junit.Assert._
import org.junit.Assert._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

abstract class CoreTest extends AbstractFabutTest with IFabutRepositoryTest {


  implicit var slickSession: SlickSession = null
  override def fabutBeforeTest() = {

    //opening test transaction
    slickSession = slickDb.createSession()
    slickSession.conn.setAutoCommit(false)
    Fabut.beforeTest(this)

  }

  override def fabutAfterTest() = {

    try {
      Fabut.afterTest()
    } finally {

      // simulating transaction
      slickSession.rollback()
      slickSession.close()
    }
  }

  override def getEntityTypes: java.util.List[Class[_]] = classOf[User] :: classOf[Invitation] :: classOf[Friendlist] :: classOf[friendlistRequest] :: classOf[Status] :: Nil
  override def getComplexTypes: java.util.List[Class[_]] = Nil
  override def getIgnoredTypes: java.util.List[Class[_]] = Nil


  override def findById(entityClass: Class[_], id: AnyRef): AnyRef =
    if (entityClass == classOf[User]) userDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == classOf[Invitation]) invitationDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == classOf[Friendlist]) friendlistDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == classOf[friendlistRequest]) friendlistRequestDao.findById(id.asInstanceOf[Int]).orNull
    else if (entityClass == classOf[Status]) statusDao.findById(id.asInstanceOf[Int]).orNull
    else throw new IllegalStateException("Unsupported entity class " + entityClass)


  override def findAll(entityClass: Class[_]): java.util.List[_] =
    if (entityClass == classOf[User]) userDao.findAll
    else if (entityClass == classOf[Invitation]) invitationDao.findAll
    else if (entityClass == classOf[Friendlist]) friendlistDao.findAll
    else if (entityClass == classOf[friendlistRequest]) friendlistRequestDao.findAll
    else if (entityClass == classOf[Status]) statusDao.findAll
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
