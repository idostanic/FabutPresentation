package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.persistence._
import org.joda.time.DateTime

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}
import scala.util.Try

class SecuredService(val userDao: UserDao, val userSessionDao: UserSessionDao, val passwordEncoder: PasswordEncoder, val appEmail: String, val appName: String, val appUrl: String) {

  def createUserSession(user: User)(implicit session: SlickSession): UserSession = {
    val userSession = new UserSession(user, passwordEncoder.encode(user.toString, new DateTime()), new DateTime().plusDays(1), passwordEncoder.encode(user.toString, new DateTime()),new DateTime().plusMonths(1))

    userSessionDao.save(userSession)

    userSession
  }

  def signOut(accessToken: String)(implicit session: SlickSession): Try[User] = Try {
    require(accessToken != null)

    val userSession = userSessionDao.findByAccessToken(accessToken).getOrElse(throw CREDENTIALS_ARE_INVALID)
    val user = userSession.user

    userSessionDao.deleteById(userSession.id)

    user
  }

  def authenticate(accessToken: String)(implicit session: SlickSession): Try[(User, UserSession)] = Try {
    require(accessToken != null)

    val userSession = userSessionDao.findByAccessToken(accessToken).getOrElse(throw CREDENTIALS_ARE_INVALID)
    if (userSession.accessTokenExpires.isAfterNow) throw ACCESS_TOKEN_IS_EXPIRED

    val user = userSession.user

    (user, userSession)
  }

  def refreshToken(refreshToken: String)(implicit session: SlickSession): Try[(User, UserSession)] = Try {
    require(refreshToken != null)

    val userSession = userSessionDao.findByRefreshToken(refreshToken).getOrElse(throw REFRESH_TOKEN_IS_INVALID)
    if (userSession.refreshTokenExpires.isAfterNow) throw REFRESH_TOKEN_IS_EXPIRED

    val user = userSession.user

    userSession.accessToken = passwordEncoder.encode(user.toString, new DateTime())
    userSession.accessTokenExpires = new DateTime().plusDays(1)

    userSession.refreshToken = passwordEncoder.encode(user.toString, new DateTime())
    userSession.refreshTokenExpires = new DateTime().plusMonths(1)

    userSessionDao.update(userSession)

    (user, userSession)
  }

}
