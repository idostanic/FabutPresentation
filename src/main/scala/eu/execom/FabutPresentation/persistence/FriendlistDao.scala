package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

case class Friendlist(private var _id: Int, private var _user1Id: Int, private var _user2Id: Int, private var _connectionDate: Date) {

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

  private var connectionDate_persisted: DateTime = connectionDate
  def connectionDatePersisted: DateTime = connectionDate_persisted

  def connectionDate: DateTime = new org.joda.time.DateTime(_connectionDate)
  def connectionDate_=(newConnectionDate: DateTime)(implicit session: SlickSession): Any = if (newConnectionDate != connectionDate) {

    if (newConnectionDate == null) throw FRIENDLIST_CONNECTIONDATE_IS_REQUIRED

    _connectionDate = new java.sql.Date(newConnectionDate.getMillis)
  }
  def user1(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === user1Id).first
  def user1_=(user1: User)(implicit session: SlickSession) = user1Id = user1.id

  def user2(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === user2Id).first
  def user2_=(user2: User)(implicit session: SlickSession) = user2Id = user2.id

  def this(entity: Friendlist) = this(entity._id, entity._user1Id, entity._user2Id, entity._connectionDate)

  def this() = this(0, 0, 0, new java.sql.Date(DateTime.now(DateTimeZone.UTC).getMillis))

  def this(user1Id: Int, user2Id: Int, connectionDate: DateTime)(implicit session: SlickSession) = {
    this()
    this.user1Id_=(user1Id)(session)
    this.user2Id_=(user2Id)(session)
    this.connectionDate_=(connectionDate)(session)
  }

  def this(user1: User, user2: User, connectionDate: DateTime)(implicit session: SlickSession) = {
    this()
    this.user1_=(user1)(session)
    this.user2_=(user2)(session)
    this.connectionDate_=(connectionDate)(session)
  }

  def persisted() = {
    id_persisted = id
    user1Id_persisted = user1Id
    user2Id_persisted = user2Id
    connectionDate_persisted = connectionDate
  }
}

object Friendlist {
  val ID: String = "_id"
  val USER1ID: String = "_user1Id"
  val USER2ID: String = "_user2Id"
  val CONNECTIONDATE: String = "_connectionDate"
}

object FRIENDLIST_CONNECTIONDATE_IS_REQUIRED extends BadRequestException("FRIENDLIST_CONNECTIONDATE_IS_REQUIRED")

object FRIENDLIST_DOESNT_EXIST extends DataConstraintException("FRIENDLIST_DOESNT_EXIST")

object FRIENDLIST_ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLIST_ID_IS_NOT_UNIQUE")

object FRIENDLIST_USER1ID_USER2ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLIST_USER1ID_USER2ID_IS_NOT_UNIQUE")

class Friendlists(tag: Tag) extends Table[Friendlist](tag, "Friendlist") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def user1Id = column[Int]("user1Id")
  def user2Id = column[Int]("user2Id")
  def connectionDate = column[Date]("connectionDate")

  val create = Friendlist.apply _
  def * = (id, user1Id, user2Id, connectionDate) <> (create.tupled, Friendlist.unapply)
  def ? = (id.?, user1Id.?, user2Id.?, connectionDate.?).shaped.<>({r=>import r._; _1.map(_=> create.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def user1= foreignKey("FRIENDLIST_USER1_FK", user1Id, TableQuery[Users])(_.id)
  def user2= foreignKey("FRIENDLIST_USER2_FK", user2Id, TableQuery[Users])(_.id)
}

class FriendlistDao extends GenericSlickDao[Friendlist] {

  def save(entity: Friendlist)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[Friendlists]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[Friendlist])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[Friendlists]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: Friendlist)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[Friendlists]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findAll()")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): Friendlist = {
    logger.trace(s".getById(id: $id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw FRIENDLIST_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[Friendlist] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByUser1Id(user1Id: Int)(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findByUser1Id(user1Id: $user1Id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.user1Id === user1Id)

    query.list
  }

  def findByUser2Id(user2Id: Int)(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findByUser2Id(user2Id: $user2Id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.user2Id === user2Id)

    query.list
  }

  def findByConnectionDate(connectionDate: DateTime)(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findByConnectionDate(connectionDate: $connectionDate)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.connectionDate === new java.sql.Date(connectionDate.getMillis))

    query.list
  }

  def findByUser1IdUser2Id(user1Id: Int, user2Id: Int)(implicit session: SlickSession): Option[Friendlist] = {
    logger.trace(s".findByUser1IdUser2Id(user1Id: $user1Id, user2Id: $user2Id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.user1Id === user1Id)
    query = query.filter(_.user2Id === user2Id)

    query.firstOption
  }

}
