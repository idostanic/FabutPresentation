package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{ Session => SlickSession }

case class Friendlist(private var _id: Int, private var _friend1Id: Int, private var _friend2Id: Int) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var friend1Id_persisted: Int = friend1Id
  def friend1IdPersisted: Int = friend1Id_persisted

  def friend1Id: Int = _friend1Id
  def friend1Id_=(newFriend1Id: Int)(implicit session: SlickSession): Any = if (newFriend1Id != friend1Id) {

    _friend1Id = newFriend1Id
  }

  private var friend2Id_persisted: Int = friend2Id
  def friend2IdPersisted: Int = friend2Id_persisted

  def friend2Id: Int = _friend2Id
  def friend2Id_=(newFriend2Id: Int)(implicit session: SlickSession): Any = if (newFriend2Id != friend2Id) {

    _friend2Id = newFriend2Id
  }
  def friend1(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === friend1Id).first
  def friend1_=(friend1: User)(implicit session: SlickSession) = friend1Id = friend1.id

  def friend2(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === friend2Id).first
  def friend2_=(friend2: User)(implicit session: SlickSession) = friend2Id = friend2.id

  def this(entity: Friendlist) = this(entity._id, entity._friend1Id, entity._friend2Id)

  def this() = this(0, 0, 0)

  def this(friend1Id: Int, friend2Id: Int)(implicit session: SlickSession) = {
    this()
    this.friend1Id_=(friend1Id)(session)
    this.friend2Id_=(friend2Id)(session)
  }

  def this(friend1: User, friend2: User)(implicit session: SlickSession) = {
    this()
    this.friend1_=(friend1)(session)
    this.friend2_=(friend2)(session)
  }

  def persisted() = {
    id_persisted = id
    friend1Id_persisted = friend1Id
    friend2Id_persisted = friend2Id
  }
}

object Friendlist {
  val ID: String = "id"
  val FRIEND1ID: String = "friend1Id"
  val FRIEND2ID: String = "friend2Id"
}

object FRIENDLIST_DOESNT_EXIST extends DataConstraintException("FRIENDLIST_DOESNT_EXIST")

object FRIENDLIST_ID_IS_NOT_UNIQUE extends DataConstraintException("FRIENDLIST_ID_IS_NOT_UNIQUE")

class Friendlists(tag: Tag) extends Table[Friendlist](tag, "Friendlist") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def friend1Id = column[Int]("friend1Id")
  def friend2Id = column[Int]("friend2Id")

  val create = Friendlist.apply _
  def * = (id, friend1Id, friend2Id) <> (create.tupled, Friendlist.unapply)
  def ? = (id.?, friend1Id.?, friend2Id.?).shaped.<>({ r => import r._; _1.map(_ => create.tupled((_1.get, _2.get, _3.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  def friend1 = foreignKey("FRIENDLIST_FRIEND1_FK", friend1Id, TableQuery[Users])(_.id)
  def friend2 = foreignKey("FRIENDLIST_FRIEND2_FK", friend2Id, TableQuery[Users])(_.id)
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

  def findByFriend1Id(friend1Id: Int)(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findByFriend1Id(friend1Id: $friend1Id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.friend1Id === friend1Id)

    query.list
  }

  def findByFriend2Id(friend2Id: Int)(implicit session: SlickSession): List[Friendlist] = {
    logger.trace(s".findByFriend2Id(friend2Id: $friend2Id)")

    var query: Query[Friendlists, Friendlists#TableElementType, Seq] = TableQuery[Friendlists]
    query = query.filter(_.friend2Id === friend2Id)

    query.list
  }

}
