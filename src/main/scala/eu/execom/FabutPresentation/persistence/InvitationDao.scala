package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

case class Invitation(private var _id: Int, private var _fromId: Int, private var _email: String, private var _status: String) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var fromId_persisted: Int = fromId
  def fromIdPersisted: Int = fromId_persisted

  def fromId: Int = _fromId
  def fromId_=(newFromId: Int)(implicit session: SlickSession): Any = if (newFromId != fromId) {

    _fromId = newFromId
  }

  private var email_persisted: String = email
  def emailPersisted: String = email_persisted

  def email: String = _email
  def email_=(newEmail: String)(implicit session: SlickSession): Any = if (newEmail != email) {

    if (newEmail == null) throw INVITATION_EMAIL_IS_REQUIRED
    if (newEmail.size < 0) throw INVITATION_EMAIL_MIN_SIZE
    if (newEmail.size > 1024) throw INVITATION_EMAIL_MAX_SIZE

    _email = newEmail
  }

  private var status_persisted: InvitationStatus = status
  def statusPersisted: InvitationStatus = status_persisted

  def status: InvitationStatus = InvitationStatus.withName(_status)
  def status_=(newStatus: InvitationStatus)(implicit session: SlickSession): Any = if (newStatus != status) {

    if (newStatus == null) throw INVITATION_STATUS_IS_REQUIRED

    _status = newStatus.name
  }
  def from(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === fromId).first
  def from_=(from: User)(implicit session: SlickSession) = fromId = from.id

  def this(entity: Invitation) = this(entity._id, entity._fromId, entity._email, entity._status)

  def this() = this(0, 0, "", InvitationStatus.PENDING.name)

  def this(fromId: Int, email: String, status: InvitationStatus)(implicit session: SlickSession) = {
    this()
    this.fromId_=(fromId)(session)
    this.email_=(email)(session)
    this.status_=(status)(session)
  }

  def this(from: User, email: String, status: InvitationStatus)(implicit session: SlickSession) = {
    this()
    this.from_=(from)(session)
    this.email_=(email)(session)
    this.status_=(status)(session)
  }

  def persisted() = {
    id_persisted = id
    fromId_persisted = fromId
    email_persisted = email
    status_persisted = status
  }
}

object Invitation {
  val ID: String = "_id"
  val FROMID: String = "_fromId"
  val EMAIL: String = "_email"
  val STATUS: String = "_status"
}

object INVITATION_EMAIL_MIN_SIZE extends DataConstraintException("INVITATION_EMAIL_MIN_SIZE")

object INVITATION_EMAIL_MAX_SIZE extends DataConstraintException("INVITATION_EMAIL_MAX_SIZE")

object INVITATION_EMAIL_IS_REQUIRED extends BadRequestException("INVITATION_EMAIL_IS_REQUIRED")

object INVITATION_STATUS_IS_REQUIRED extends BadRequestException("INVITATION_STATUS_IS_REQUIRED")

object INVITATION_DOESNT_EXIST extends DataConstraintException("INVITATION_DOESNT_EXIST")

object INVITATION_ID_IS_NOT_UNIQUE extends DataConstraintException("INVITATION_ID_IS_NOT_UNIQUE")

class Invitations(tag: Tag) extends Table[Invitation](tag, "Invitation") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def fromId = column[Int]("fromId")
  def email = column[String]("email")
  def status = column[String]("status")

  val create = Invitation.apply _
  def * = (id, fromId, email, status) <> (create.tupled, Invitation.unapply)
  def ? = (id.?, fromId.?, email.?, status.?).shaped.<>({r=>import r._; _1.map(_=> create.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def from= foreignKey("INVITATION_FROM_FK", fromId, TableQuery[Users])(_.id)
}

class InvitationDao extends GenericSlickDao[Invitation] {

  def save(entity: Invitation)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[Invitations]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[Invitation])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[Invitations]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: Invitation)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[Invitations]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[Invitation] = {
    logger.trace(s".findAll()")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): Invitation = {
    logger.trace(s".getById(id: $id)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw INVITATION_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[Invitation] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByFromId(fromId: Int)(implicit session: SlickSession): List[Invitation] = {
    logger.trace(s".findByFromId(fromId: $fromId)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.fromId === fromId)

    query.list
  }

  def findByEmail(email: String)(implicit session: SlickSession): List[Invitation] = {
    logger.trace(s".findByEmail(email: $email)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.email === email)

    query.list
  }

  def findByStatus(status: InvitationStatus)(implicit session: SlickSession): List[Invitation] = {
    logger.trace(s".findByStatus(status: $status)")

    var query: Query[Invitations, Invitations#TableElementType, Seq] = TableQuery[Invitations]
    query = query.filter(_.status === status.name)

    query.list
  }

}
