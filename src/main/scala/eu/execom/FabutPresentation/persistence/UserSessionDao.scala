package eu.execom.FabutPresentation.persistence

import java.sql.Blob
import java.sql.Timestamp
import java.sql.Date

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

case class UserSession(private var _id: Int, private var _userId: Int, private var _accessToken: String, private var _accessTokenExpires: Date, private var _refreshToken: String, private var _refreshTokenExpires: Date) {

  private var id_persisted: Int = id
  def idPersisted: Int = id_persisted

  def id: Int = _id
  def id_=(newId: Int)(implicit session: SlickSession): Any = if (newId != id) {

    _id = newId
  }

  private var userId_persisted: Int = userId
  def userIdPersisted: Int = userId_persisted

  def userId: Int = _userId
  def userId_=(newUserId: Int)(implicit session: SlickSession): Any = if (newUserId != userId) {

    _userId = newUserId
  }

  private var accessToken_persisted: String = accessToken
  def accessTokenPersisted: String = accessToken_persisted

  def accessToken: String = _accessToken
  def accessToken_=(newAccessToken: String)(implicit session: SlickSession): Any = if (newAccessToken != accessToken) {

    if (newAccessToken == null) throw USER_SESSION_ACCESS_TOKEN_IS_REQUIRED
    if (TableQuery[UserSessions].filter(_.accessToken === newAccessToken).exists.run) throw USER_SESSION_ACCESS_TOKEN_IS_NOT_UNIQUE
    if (newAccessToken.size < 0) throw USER_SESSION_ACCESS_TOKEN_MIN_SIZE
    if (newAccessToken.size > 128) throw USER_SESSION_ACCESS_TOKEN_MAX_SIZE

    _accessToken = newAccessToken
  }

  private var accessTokenExpires_persisted: DateTime = accessTokenExpires
  def accessTokenExpiresPersisted: DateTime = accessTokenExpires_persisted

  def accessTokenExpires: DateTime = new org.joda.time.DateTime(_accessTokenExpires)
  def accessTokenExpires_=(newAccessTokenExpires: DateTime)(implicit session: SlickSession): Any = if (newAccessTokenExpires != accessTokenExpires) {

    if (newAccessTokenExpires == null) throw USER_SESSION_ACCESS_TOKEN_EXPIRES_IS_REQUIRED

    _accessTokenExpires = new java.sql.Date(newAccessTokenExpires.getMillis)
  }

  private var refreshToken_persisted: String = refreshToken
  def refreshTokenPersisted: String = refreshToken_persisted

  def refreshToken: String = _refreshToken
  def refreshToken_=(newRefreshToken: String)(implicit session: SlickSession): Any = if (newRefreshToken != refreshToken) {

    if (newRefreshToken == null) throw USER_SESSION_REFRESH_TOKEN_IS_REQUIRED
    if (TableQuery[UserSessions].filter(_.refreshToken === newRefreshToken).exists.run) throw USER_SESSION_REFRESH_TOKEN_IS_NOT_UNIQUE
    if (newRefreshToken.size < 0) throw USER_SESSION_REFRESH_TOKEN_MIN_SIZE
    if (newRefreshToken.size > 128) throw USER_SESSION_REFRESH_TOKEN_MAX_SIZE

    _refreshToken = newRefreshToken
  }

  private var refreshTokenExpires_persisted: DateTime = refreshTokenExpires
  def refreshTokenExpiresPersisted: DateTime = refreshTokenExpires_persisted

  def refreshTokenExpires: DateTime = new org.joda.time.DateTime(_refreshTokenExpires)
  def refreshTokenExpires_=(newRefreshTokenExpires: DateTime)(implicit session: SlickSession): Any = if (newRefreshTokenExpires != refreshTokenExpires) {

    if (newRefreshTokenExpires == null) throw USER_SESSION_REFRESH_TOKEN_EXPIRES_IS_REQUIRED

    _refreshTokenExpires = new java.sql.Date(newRefreshTokenExpires.getMillis)
  }
  def user(implicit session: SlickSession): User = TableQuery[Users].filter(_.id === userId).first
  def user_=(user: User)(implicit session: SlickSession) = userId = user.id

  def this(entity: UserSession) = this(entity._id, entity._userId, entity._accessToken, entity._accessTokenExpires, entity._refreshToken, entity._refreshTokenExpires)

  def this() = this(0, 0, "", new java.sql.Date(DateTime.now(DateTimeZone.UTC).getMillis), "", new java.sql.Date(DateTime.now(DateTimeZone.UTC).getMillis))

  def this(userId: Int, accessToken: String, accessTokenExpires: DateTime, refreshToken: String, refreshTokenExpires: DateTime)(implicit session: SlickSession) = {
    this()
    this.userId_=(userId)(session)
    this.accessToken_=(accessToken)(session)
    this.accessTokenExpires_=(accessTokenExpires)(session)
    this.refreshToken_=(refreshToken)(session)
    this.refreshTokenExpires_=(refreshTokenExpires)(session)
  }

  def this(user: User, accessToken: String, accessTokenExpires: DateTime, refreshToken: String, refreshTokenExpires: DateTime)(implicit session: SlickSession) = {
    this()
    this.user_=(user)(session)
    this.accessToken_=(accessToken)(session)
    this.accessTokenExpires_=(accessTokenExpires)(session)
    this.refreshToken_=(refreshToken)(session)
    this.refreshTokenExpires_=(refreshTokenExpires)(session)
  }

  def persisted() = {
    id_persisted = id
    userId_persisted = userId
    accessToken_persisted = accessToken
    accessTokenExpires_persisted = accessTokenExpires
    refreshToken_persisted = refreshToken
    refreshTokenExpires_persisted = refreshTokenExpires
  }
}

object UserSession {
  val ID: String = "id"
  val USERID: String = "userId"
  val ACCESSTOKEN: String = "accessToken"
  val ACCESSTOKENEXPIRES: String = "accessTokenExpires"
  val REFRESHTOKEN: String = "refreshToken"
  val REFRESHTOKENEXPIRES: String = "refreshTokenExpires"
}

object USER_SESSION_ACCESS_TOKEN_IS_REQUIRED extends DataConstraintException("USER_SESSION_ACCESS_TOKEN_IS_REQUIRED")

object USER_SESSION_ACCESS_TOKEN_MIN_SIZE extends DataConstraintException("USER_SESSION_ACCESS_TOKEN_MIN_SIZE")

object USER_SESSION_ACCESS_TOKEN_MAX_SIZE extends DataConstraintException("USER_SESSION_ACCESS_TOKEN_MAX_SIZE")

object USER_SESSION_ACCESS_TOKEN_EXPIRES_IS_REQUIRED extends DataConstraintException("USER_SESSION_ACCESS_TOKEN_EXPIRES_IS_REQUIRED")

object USER_SESSION_REFRESH_TOKEN_IS_REQUIRED extends DataConstraintException("USER_SESSION_REFRESH_TOKEN_IS_REQUIRED")

object USER_SESSION_REFRESH_TOKEN_MIN_SIZE extends DataConstraintException("USER_SESSION_REFRESH_TOKEN_MIN_SIZE")

object USER_SESSION_REFRESH_TOKEN_MAX_SIZE extends DataConstraintException("USER_SESSION_REFRESH_TOKEN_MAX_SIZE")

object USER_SESSION_REFRESH_TOKEN_EXPIRES_IS_REQUIRED extends DataConstraintException("USER_SESSION_REFRESH_TOKEN_EXPIRES_IS_REQUIRED")

object USERSESSION_DOESNT_EXIST extends DataConstraintException("USERSESSION_DOESNT_EXIST")

object USER_SESSION_ID_IS_NOT_UNIQUE extends DataConstraintException("USER_SESSION_ID_IS_NOT_UNIQUE")

object USER_SESSION_ACCESS_TOKEN_IS_NOT_UNIQUE extends DataConstraintException("USER_SESSION_ACCESS_TOKEN_IS_NOT_UNIQUE")

object USER_SESSION_REFRESH_TOKEN_IS_NOT_UNIQUE extends DataConstraintException("USER_SESSION_REFRESH_TOKEN_IS_NOT_UNIQUE")

class UserSessions(tag: Tag) extends Table[UserSession](tag, "UserSession") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("userId")
  def accessToken = column[String]("accessToken")
  def accessTokenExpires = column[Date]("accessTokenExpires")
  def refreshToken = column[String]("refreshToken")
  def refreshTokenExpires = column[Date]("refreshTokenExpires")

  val create = UserSession.apply _
  def * = (id, userId, accessToken, accessTokenExpires, refreshToken, refreshTokenExpires) <> (create.tupled, UserSession.unapply)
  def ? = (id.?, userId.?, accessToken.?, accessTokenExpires.?, refreshToken.?, refreshTokenExpires.?).shaped.<>({r=>import r._; _1.map(_=> create.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  def user= foreignKey("USERSESSION_USER_FK", userId, TableQuery[Users])(_.id)
}

class UserSessionDao extends GenericSlickDao[UserSession] {

  def save(entity: UserSession)(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entity: $entity)")
    val tableQuery = TableQuery[UserSessions]
    val id = tableQuery returning tableQuery.map(_.id) += entity
    entity.id = id
    entity.persisted()
  }

  def save(entities: List[UserSession])(implicit session: SlickSession): Unit = {
    logger.trace(s".save(entities: $entities)")
    val tableQuery = TableQuery[UserSessions]
    val ids = tableQuery returning tableQuery.map(_.id) ++= entities
    ids.zip(entities).foreach(idWithEntity => {
      val id = idWithEntity._1
      val entity = idWithEntity._2
      entity.id = id
      entity.persisted()
    })

  }

  def update(entity: UserSession)(implicit session: SlickSession): Unit = {
    logger.trace(s".update(entity: $entity)")
    val tableQuery = TableQuery[UserSessions]
    tableQuery.filter(_.id === entity.id).update(entity)
    entity.persisted()
  }

  def findAll()(implicit session: SlickSession): List[UserSession] = {
    logger.trace(s".findAll()")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]

    query.list
  }

  def countAll()(implicit session: SlickSession): Int = {
    logger.trace(s".countAll()")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]

    query.length.run
  }

  def getById(id: Int)(implicit session: SlickSession): UserSession = {
    logger.trace(s".getById(id: $id)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.id === id)

    query.firstOption.getOrElse(throw USERSESSION_DOESNT_EXIST)
  }

  def deleteById(id: Int)(implicit session: SlickSession): Boolean = {
    logger.trace(s".deleteById(id: $id)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.id === id)

    query.delete != 0
  }

  def findById(id: Int)(implicit session: SlickSession): Option[UserSession] = {
    logger.trace(s".findById(id: $id)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.id === id)

    query.firstOption
  }

  def findByUserId(userId: Int)(implicit session: SlickSession): List[UserSession] = {
    logger.trace(s".findByUserId(userId: $userId)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.userId === userId)

    query.list
  }

  def findByAccessToken(accessToken: String)(implicit session: SlickSession): Option[UserSession] = {
    logger.trace(s".findByAccessToken(accessToken: $accessToken)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.accessToken === accessToken)

    query.firstOption
  }

  def findByAccessTokenExpires(accessTokenExpires: DateTime)(implicit session: SlickSession): List[UserSession] = {
    logger.trace(s".findByAccessTokenExpires(accessTokenExpires: $accessTokenExpires)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.accessTokenExpires === new java.sql.Date(accessTokenExpires.getMillis))

    query.list
  }

  def findByRefreshToken(refreshToken: String)(implicit session: SlickSession): Option[UserSession] = {
    logger.trace(s".findByRefreshToken(refreshToken: $refreshToken)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.refreshToken === refreshToken)

    query.firstOption
  }

  def findByRefreshTokenExpires(refreshTokenExpires: DateTime)(implicit session: SlickSession): List[UserSession] = {
    logger.trace(s".findByRefreshTokenExpires(refreshTokenExpires: $refreshTokenExpires)")

    var query: Query[UserSessions, UserSessions#TableElementType, Seq] = TableQuery[UserSessions]
    query = query.filter(_.refreshTokenExpires === new java.sql.Date(refreshTokenExpires.getMillis))

    query.list
  }

}
