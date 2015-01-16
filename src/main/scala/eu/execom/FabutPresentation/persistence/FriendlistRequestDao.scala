package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

case class FriendlistRequest(private var _id: Int, private var _user1Id: Int, private var _user2Id: Int, private var _status: String) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var user1Id_persisted: Int = user1Id
  def user1IdPersisted: Int = user1Id_persisted

  def user1Id: Int = _user1Id
  def user1Id_=(newUser1Id: Int)(implicit session: SlickSession): Any = if (newUser1Id != user1Id) {

    _user1Id = newUser1Id
  }

  private var user2Id_persisted: Int = user2Id
  def user2IdPersisted: Int = user2Id_persisted

  def user2Id: Int = _user2Id
  def user2Id_=(newUser2Id: Int)(implicit session: SlickSession): Any = if (newUser2Id != user2Id) {

    _user2Id = newUser2Id
  }

  private var status_persisted: FriendRequestStatus = status
  def statusPersisted: FriendRequestStatus = status_persisted

  def status: FriendRequestStatus = FriendRequestStatus.withName(_status)
  def status_=(newStatus: FriendRequestStatus)(implicit session: SlickSession): Any = if (newStatus != status) {

    if (newStatus == null) throw FRIENDLIST_REQUEST_STATUS_IS_REQUIRED

    _status = newStatus.name
  }
  def user1(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === user1Id).first
  def user1_=(user1: User)(implicit session: SlickSession) = user1Id = user1.id

  def user2(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === user2Id).first
  def user2_=(user2: User)(implicit session: SlickSession) = user2Id = user2.id

  def this(entity: FriendlistRequest) = this(entity._id, entity._user1Id, entity._user2Id, entity._status)

  def this() = this(0, 0, 0, FriendRequestStatus.PENDING.name)

  def this(user1Id: Int, user2Id: Int, status: FriendRequestStatus)(implicit session: SlickSession) = {
    this()
    this.user1Id_=(user1Id)(session)
    this.user2Id_=(user2Id)(session)
    this.status_=(status)(session)
  }

  def this(user1: User, user2: User, status: FriendRequestStatus)(implicit session: SlickSession) = {
    this()
    this.user1_=(user1)(session)
    this.user2_=(user2)(session)
    this.status_=(status)(session)
  }

  def persisted() = {
    id_persisted = id
    user1Id_persisted = user1Id
    user2Id_persisted = user2Id
    status_persisted = status
  }
}

object FriendlistRequest {
  val ID: String = "id"
  val USER1ID: String = "user1Id"
  val USER2ID: String = "user2Id"
  val STATUS: String = "status"
}

object FRIENDLIST_REQUEST_STATUS_IS_REQUIRED extends DataConstraintException("FRIENDLIST_REQUEST_STATUS_IS_REQUIRED")

object FRIENDLISTREQUEST_DOESNT_EXIST extends DataConstraintException("FRIENDLISTREQUEST_DOESNT_EXIST")

object FRIENDLIST_REQUEST_ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLIST_REQUEST_ID_IS_NOT_UNIQUE")

object FRIENDLIST_REQUEST_USER_1_ID_USER_2_ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLIST_REQUEST_USER_1_ID_USER_2_ID_IS_NOT_UNIQUE")

class FriendlistRequests(tag: Tag) extends Table[FriendlistRequest](tag, "FriendlistRequest") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def user1Id = column[Int]("user1Id")
  def user2Id = column[Int]("user2Id")
  def status = column[String]("status")

  val create = FriendlistRequest.apply _
  def * = (id, user1Id, user2Id, status) <> (create.tupled, FriendlistRequest.unapply)
  def ? = (id.?, user1Id.?, user2Id.?, status.?).shaped.<>({r=>import r._; _1.map(_=> create.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def user1= foreignKey("FRIENDLISTREQUEST_USER1_FK", user1Id, TableQuery[Users])(_.id)
  def user2= foreignKey("FRIENDLISTREQUEST_USER2_FK", user2Id, TableQuery[Users])(_.id)
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

  def findByUser1Id(user1Id: Int)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByUser1Id(user1Id: $user1Id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.user1Id === user1Id)

    query.list
  }

  def findByUser2Id(user2Id: Int)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByUser2Id(user2Id: $user2Id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.user2Id === user2Id)

    query.list
  }

  def findByStatus(status: FriendRequestStatus)(implicit session: SlickSession): List[FriendlistRequest] = {
    logger.trace(s".findByStatus(status: $status)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.status === status.name)

    query.list
  }

  def findByUser1IdUser2Id(user1Id: Int, user2Id: Int)(implicit session: SlickSession): Option[FriendlistRequest] = {
    logger.trace(s".findByUser1IdUser2Id(user1Id: $user1Id, user2Id: $user2Id)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.user1Id === user1Id)
    query = query.filter(_.user2Id === user2Id)

    query.firstOption
  }

  def findByRequesterIDRequesteeID(requesterID: Int, requesteeID: Int)(implicit session: SlickSession): Option[FriendlistRequest] = {
    logger.trace(s".findByRequesterIDRequesteeID(requesterID: $requesterID, requesteeID: $requesteeID)")

    var query: Query[FriendlistRequests, FriendlistRequests#TableElementType, Seq] = TableQuery[FriendlistRequests]
    query = query.filter(_.user1Id === requesterID)
    query = query.filter(_.user2Id === requesteeID)

    query.firstOption
  }

}
