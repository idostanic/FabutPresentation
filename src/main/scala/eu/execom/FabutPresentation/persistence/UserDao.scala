package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

case class User(private var _id: Int, private var _firstName: String, private var _lastName: Option[String], private var _email: String, private var _numberOfFriends: Int) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var firstName_persisted: String = firstName
  def firstNamePersisted: String = firstName_persisted

  def firstName: String = _firstName
  def firstName_=(newFirstName: String)(implicit session: SlickSession): Any = if (newFirstName != firstName) {

    if (newFirstName == null) throw USER_FIRST_NAME_IS_REQUIRED
    if (newFirstName.size < 0) throw USER_FIRST_NAME_MIN_SIZE
    if (newFirstName.size > 1024) throw USER_FIRST_NAME_MAX_SIZE

    _firstName = newFirstName
  }

  private var lastName_persisted: Option[String] = lastName
  def lastNamePersisted: Option[String] = lastName_persisted

  def lastName: Option[String] = _lastName
  def lastName_=(newLastName: Option[String])(implicit session: SlickSession): Any = if (newLastName != lastName) {
    if (newLastName.isDefined) {
      if (newLastName.get.size < 0) throw USER_LAST_NAME_MIN_SIZE
      if (newLastName.get.size > 1024) throw USER_LAST_NAME_MAX_SIZE
    }
    _lastName = newLastName
  }

  private var email_persisted: String = email
  def emailPersisted: String = email_persisted

  def email: String = _email
  def email_=(newEmail: String)(implicit session: SlickSession): Any = if (newEmail != email) {

    if (newEmail == null) throw USER_EMAIL_IS_REQUIRED
    if (TableQuery[Users].filter(_.email === newEmail).exists.run) throw USER_EMAIL_IS_NOT_UNIQUE
    if (newEmail.size < 0) throw USER_EMAIL_MIN_SIZE
    if (newEmail.size > 50) throw USER_EMAIL_MAX_SIZE

    _email = newEmail
  }

  private var numberOfFriends_persisted: Int = numberOfFriends
  def numberOfFriendsPersisted: Int = numberOfFriends_persisted

  def numberOfFriends: Int = _numberOfFriends
  def numberOfFriends_=(newNumberOfFriends: Int)(implicit session: SlickSession): Any = if (newNumberOfFriends != numberOfFriends) {

    _numberOfFriends = newNumberOfFriends
  }


  def this(entity: User) = this(entity._id, entity._firstName, entity._lastName, entity._email, entity._numberOfFriends)

  def this() = this(0, "", None, "", 0)

  def this(firstName: String, lastName: Option[String], email: String, numberOfFriends: Int)(implicit session: SlickSession) = {
    this()
    this.firstName_=(firstName)(session)
    this.lastName_=(lastName)(session)
    this.email_=(email)(session)
    this.numberOfFriends_=(numberOfFriends)(session)
  }

  def persisted() = {
    id_persisted = id
    firstName_persisted = firstName
    lastName_persisted = lastName
    email_persisted = email
    numberOfFriends_persisted = numberOfFriends
  }
}

object User {
  val ID: String = "id"
  val FIRSTNAME: String = "firstName"
  val LASTNAME: String = "lastName"
  val EMAIL: String = "email"
  val NUMBEROFFRIENDS: String = "numberOfFriends"
}

object USER_FIRST_NAME_IS_REQUIRED extends DataConstraintException("USER_FIRST_NAME_IS_REQUIRED")

object USER_FIRST_NAME_MIN_SIZE extends DataConstraintException("USER_FIRST_NAME_MIN_SIZE")

object USER_FIRST_NAME_MAX_SIZE extends DataConstraintException("USER_FIRST_NAME_MAX_SIZE")

object USER_LAST_NAME_MIN_SIZE extends DataConstraintException("USER_LAST_NAME_MIN_SIZE")

object USER_LAST_NAME_MAX_SIZE extends DataConstraintException("USER_LAST_NAME_MAX_SIZE")

object USER_EMAIL_IS_REQUIRED extends DataConstraintException("USER_EMAIL_IS_REQUIRED")

object USER_EMAIL_MIN_SIZE extends DataConstraintException("USER_EMAIL_MIN_SIZE")

object USER_EMAIL_MAX_SIZE extends DataConstraintException("USER_EMAIL_MAX_SIZE")

object USER_DOESNT_EXIST extends DataConstraintException("USER_DOESNT_EXIST")

object USER_ID_IS_NOT_UNIQUE extends DataConstraintException("USER_ID_IS_NOT_UNIQUE")

object USER_EMAIL_IS_NOT_UNIQUE extends DataConstraintException("USER_EMAIL_IS_NOT_UNIQUE")

class Users(tag: Tag) extends Table[User](tag, "User") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("firstName")
  def lastName = column[Option[String]]("lastName")
  def email = column[String]("email")
  def numberOfFriends = column[Int]("numberOfFriends")

  val create = User.apply _
  def * = (id, firstName, lastName, email, numberOfFriends) <> (create.tupled, User.unapply)
  def ? = (id.?, firstName.?, lastName, email.?, numberOfFriends.?).shaped.<>({r=>import r._; _1.map(_=> create.tupled((_1.get, _2.get, _3, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))


}

class UserDao extends GenericSlickDao[User] {

  def save(entity: User)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[Users]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[User])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[Users]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: User)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[Users]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[User] = {
    logger.trace(s".findAll()")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): User = {
    logger.trace(s".getById(id: $id)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw USER_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[User] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByFirstName(firstName: String)(implicit session: SlickSession): List[User] = {
    logger.trace(s".findByFirstName(firstName: $firstName)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.firstName === firstName)

    query.list
  }

  def findByLastName(lastName: Option[String])(implicit session: SlickSession): List[User] = {
    logger.trace(s".findByLastName(lastName: $lastName)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    lastName match {
      case Some(value) => query = query.filter(_.lastName === value)
      case None => query = query.filter(_.lastName.isEmpty)
    }

    query.list
  }

  def findByEmail(email: String)(implicit session: SlickSession): Option[User] = {
    logger.trace(s".findByEmail(email: $email)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.email === email)

    query.firstOption
  }

  def findByNumberOfFriends(numberOfFriends: Int)(implicit session: SlickSession): List[User] = {
    logger.trace(s".findByNumberOfFriends(numberOfFriends: $numberOfFriends)")

    var query: Query[Users, Users#TableElementType, Seq] = TableQuery[Users]
    query = query.filter(_.numberOfFriends === numberOfFriends)

    query.list
  }

}
