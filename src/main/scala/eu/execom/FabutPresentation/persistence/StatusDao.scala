package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }

case class Status(private var _id: Int, private var _content: String, private var _fromIdId: Int, private var _creationDate: Date) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var content_persisted: String = content
  def contentPersisted: String = content_persisted

  def content: String = _content
  def content_=(newContent: String)(implicit session: SlickSession): Any = if (newContent != content) {

    if (newContent == null) throw STATUS_CONTENT_IS_REQUIRED
    if (newContent.size < 0) throw STATUS_CONTENT_MIN_SIZE
    if (newContent.size > 1024) throw STATUS_CONTENT_MAX_SIZE

    _content = newContent
  }

  private var fromIdId_persisted: Int = fromIdId
  def fromIdIdPersisted: Int = fromIdId_persisted

  def fromIdId: Int = _fromIdId
  def fromIdId_=(newFromIdId: Int)(implicit session: SlickSession): Any = if (newFromIdId != fromIdId) {

    _fromIdId = newFromIdId
  }

  private var creationDate_persisted: DateTime = creationDate
  def creationDatePersisted: DateTime = creationDate_persisted

  def creationDate: DateTime = new org.joda.time.DateTime(_creationDate)
  def creationDate_=(newCreationDate: DateTime)(implicit session: SlickSession): Any = if (newCreationDate != creationDate) {

    if (newCreationDate == null) throw STATUS_CREATIONDATE_IS_REQUIRED

    _creationDate = new java.sql.Date(newCreationDate.getMillis)
  }
  def fromId(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === fromIdId).first
  def fromId_=(fromId: User)(implicit session: SlickSession) = fromIdId = fromId.id

  def this(entity: Status) = this(entity._id, entity._content, entity._fromIdId, entity._creationDate)

  def this() = this(0, "", 0, new java.sql.Date(DateTime.now(DateTimeZone.UTC).getMillis))

  def this(content: String, fromIdId: Int, creationDate: DateTime)(implicit session: SlickSession) = {
    this()
    this.content_=(content)(session)
    this.fromIdId_=(fromIdId)(session)
    this.creationDate_=(creationDate)(session)
  }

  def this(content: String, fromId: User, creationDate: DateTime)(implicit session: SlickSession) = {
    this()
    this.content_=(content)(session)
    this.fromId_=(fromId)(session)
    this.creationDate_=(creationDate)(session)
  }

  def persisted() = {
    id_persisted = id
    content_persisted = content
    fromIdId_persisted = fromIdId
    creationDate_persisted = creationDate
  }
}

object Status {
  val ID: String = "id"
  val CONTENT: String = "content"
  val FROMIDID: String = "fromIdId"
  val CREATIONDATE: String = "creationDate"
}

object STATUS_CONTENT_MIN_SIZE extends DataConstraintException("STATUS_CONTENT_MIN_SIZE")

object STATUS_CONTENT_MAX_SIZE extends DataConstraintException("STATUS_CONTENT_MAX_SIZE")

object STATUS_CONTENT_IS_REQUIRED extends BadRequestException("STATUS_CONTENT_IS_REQUIRED")

object STATUS_CREATIONDATE_IS_REQUIRED extends BadRequestException("STATUS_CREATIONDATE_IS_REQUIRED")

object STATUS_DOESNT_EXIST extends DataConstraintException("STATUS_DOESNT_EXIST")

object STATUS_ID_IS_NOT_UNIQUE extends DataConstraintException("STATUS_ID_IS_NOT_UNIQUE")

class Statuss(tag: Tag) extends Table[Status](tag, "Status") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def content = column[String]("content")
  def fromIdId = column[Int]("fromIdId")
  def creationDate = column[Date]("creationDate")

  val create = Status.apply _
  def * = (id, content, fromIdId, creationDate) <> (create.tupled, Status.unapply)
  def ? = (id.?, content.?, fromIdId.?, creationDate.?).shaped.<>({ r => import r._; _1.map(_ => create.tupled((_1.get, _2.get, _3.get, _4.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  def fromId = foreignKey("STATUS_FROMID_FK", fromIdId, TableQuery[Users])(_.id)
}

class StatusDao extends GenericSlickDao[Status] {

  def save(entity: Status)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[Statuss]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[Status])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[Statuss]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: Status)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[Statuss]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[Status] = {
    logger.trace(s".findAll()")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): Status = {
    logger.trace(s".getById(id: $id)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw STATUS_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[Status] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByContent(content: String)(implicit session: SlickSession): List[Status] = {
    logger.trace(s".findByContent(content: $content)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.content === content)

    query.list
  }

  def findByFromIdId(fromIdId: Int)(implicit session: SlickSession): List[Status] = {
    logger.trace(s".findByFromIdId(fromIdId: $fromIdId)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.fromIdId === fromIdId)

    query.list
  }

  def findByCreationDate(creationDate: DateTime)(implicit session: SlickSession): List[Status] = {
    logger.trace(s".findByCreationDate(creationDate: $creationDate)")

    var query: Query[Statuss, Statuss#TableElementType, Seq] = TableQuery[Statuss]
    query = query.filter(_.creationDate === new java.sql.Date(creationDate.getMillis))

    query.list
  }

}
