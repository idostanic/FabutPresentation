package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }

case class FriendlistRequest(private var _id: Int, private var _requesterId: Int, private var _requesteeId: Int, private var _status: String) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var requesterId_persisted: Int = requesterId
  def requesterIdPersisted: Int = requesterId_persisted

  def requesterId: Int = _requesterId
  def requesterId_=(newRequesterId: Int)(implicit session: SlickSession): Any = if (newRequesterId != requesterId) {

    _requesterId = newRequesterId
  }

  private var requesteeId_persisted: Int = requesteeId
  def requesteeIdPersisted: Int = requesteeId_persisted

  def requesteeId: Int = _requesteeId
  def requesteeId_=(newRequesteeId: Int)(implicit session: SlickSession): Any = if (newRequesteeId != requesteeId) {

    _requesteeId = newRequesteeId
  }

  private var status_persisted: FriendRequestStatus = status
  def statusPersisted: FriendRequestStatus = status_persisted

  def status: FriendRequestStatus = FriendRequestStatus.withName(_status)
  def status_=(newStatus: FriendRequestStatus)(implicit session: SlickSession): Any = if (newStatus != status) {

    if (newStatus == null) throw FRIENDLISTREQUEST_STATUS_IS_REQUIRED

    _status = newStatus.name
  }
  def requester(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === requesterId).first
  def requester_=(requester: User)(implicit session: SlickSession) = requesterId = requester.id

  def requestee(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === requesteeId).first
  def requestee_=(requestee: User)(implicit session: SlickSession) = requesteeId = requestee.id

  def this(entity: FriendlistRequest) = this(entity._id, entity._requesterId, entity._requesteeId, entity._status)

  def this() = this(0, 0, 0, FriendRequestStatus.PENDING.name)

  def this(requesterId: Int, requesteeId: Int, status: FriendRequestStatus)(implicit session: SlickSession) = {
    this()
    this.requesterId_=(requesterId)(session)
    this.requesteeId_=(requesteeId)(session)
    this.status_=(status)(session)
  }

  def this(requester: User, requestee: User, status: FriendRequestStatus)(implicit session: SlickSession) = {
    this()
    this.requester_=(requester)(session)
    this.requestee_=(requestee)(session)
    this.status_=(status)(session)
  }

  def persisted() = {
    id_persisted = id
    requesterId_persisted = requesterId
    requesteeId_persisted = requesteeId
    status_persisted = status
  }
}

object FriendlistRequest {
  val ID: String = "id"
  val REQUESTERID: String = "requesterId"
  val REQUESTEEID: String = "requesteeId"
  val STATUS: String = "status"
}

object FRIENDLISTREQUEST_STATUS_IS_REQUIRED extends BadRequestException("FRIENDLISTREQUEST_STATUS_IS_REQUIRED")

object FRIENDLISTREQUEST_DOESNT_EXIST extends DataConstraintException("FRIENDLISTREQUEST_DOESNT_EXIST")

object FRIENDLISTREQUEST_ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLISTREQUEST_ID_IS_NOT_UNIQUE")

object FRIENDLISTREQUEST_REQUESTERID_REQUESTEEID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLISTREQUEST_REQUESTERID_REQUESTEEID_IS_NOT_UNIQUE")

class FriendlistRequests(tag: Tag) extends Table[FriendlistRequest](tag, "FriendlistRequest") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def requesterId = column[Int]("requesterId")
  def requesteeId = column[Int]("requesteeId")
  def status = column[String]("status")

  val create = FriendlistRequest.apply _
  def * = (id, requesterId, requesteeId, status) <> (create.tupled, FriendlistRequest.unapply)
  def ? = (id.?, requesterId.?, requesteeId.?, status.?).shaped.<>({ r => import r._; _1.map(_ => create.tupled((_1.get, _2.get, _3.get, _4.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  def requester = foreignKey("FRIENDLISTREQUEST_REQUESTER_FK", requesterId, TableQuery[Users])(_.id)
  def requestee = foreignKey("FRIENDLISTREQUEST_REQUESTEE_FK", requesteeId, TableQuery[Users])(_.id)
}

class FriendlistRequestDao extends GenericSlickDao[FriendlistRequest] {

  def save(entity: FriendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[FriendlistRequests]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[FriendlistRequest])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[FriendlistRequests]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: FriendlistRequest)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[FriendlistRequests]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findAll()")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): FriendlistRequest = {
    logger.trace(s".getById(id: $id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw FRIENDLISTREQUEST_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[FriendlistRequest] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByRequesterId(requesterId: Int)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByRequesterId(requesterId: $requesterId)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.requesterId === requesterId)

    query.list
  }

  def findByRequesteeId(requesteeId: Int)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByRequesteeId(requesteeId: $requesteeId)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.requesteeId === requesteeId)

    query.list
  }

  def findByStatus(status: FriendRequestStatus)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByStatus(status: $status)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.status === status.name)

    query.list
  }

  def findByRequesterIdRequesteeId(requesterId: Int, requesteeId: Int)(implicit session: SlickSession): Option[FriendlistRequest] = {
    logger.trace(s".findByRequesterIdRequesteeId(requesterId: $requesterId, requesteeId: $requesteeId)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.requesterId === requesterId)
    query = query.filter(_.requesteeId === requesteeId)

    query.firstOption
  }

}
