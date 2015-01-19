package eu.execom.FabutPresentation.api

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.service.SecuredService
import eu.execom.FabutPresentation.util._
import org.joda.time._

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}
import scala.util._

class AuthenticationApi(val userDao: UserDao, val securedService: SecuredService) extends SecuredApi with Logging {

  def signOut(authenticationCode: String)(implicit slickSession: SlickSession): Try[Unit] = Try {
    logger.trace(s".signOut(authenticationCode: $authenticationCode)")
    secure(authenticationCode) { implicit user: User =>

      securedService.signOut(authenticationCode)
    }
  }

  def authenticate(authenticationDto: AccessTokenDto)(implicit slickSession: SlickSession): Try[AuthenticationResponseDto] = Try {
    logger.trace(s".authenticate(authenticationDto: $authenticationDto)")

    val (user, client) = securedService.authenticate(authenticationDto.accessToken).get
    new AuthenticationResponseDto(client.accessToken, client.refreshToken)
  }

  def refreshToken(refreshTokenDto: RefreshTokenDto)(implicit slickSession: SlickSession): Try[AuthenticationResponseDto] = Try {
    logger.trace(s".refreshToken(refreshTokenDto: $refreshTokenDto)")

    val (user, client) = securedService.refreshToken(refreshTokenDto.refreshToken).get
    new AuthenticationResponseDto(client.accessToken, client.refreshToken)
  }

}

case class AuthenticationResponseDto(accessToken: String, refreshToken: String) {

  if (accessToken == null) throw AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_IS_REQUIRED
  if (accessToken.size < 0) throw AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MIN_SIZE
  if (accessToken.size > 128) throw AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MAX_SIZE

  if (refreshToken == null) throw AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_IS_REQUIRED
  if (refreshToken.size < 0) throw AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MIN_SIZE
  if (refreshToken.size > 128) throw AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MAX_SIZE
}

object AuthenticationResponseDto {
  val ACCESSTOKEN: String = "accessToken"
  val REFRESHTOKEN: String = "refreshToken"
}

object AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_IS_REQUIRED extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_IS_REQUIRED")

object AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MIN_SIZE extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MIN_SIZE")

object AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MAX_SIZE extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_ACCESS_TOKEN_MAX_SIZE")

object AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_IS_REQUIRED extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_IS_REQUIRED")

object AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MIN_SIZE extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MIN_SIZE")

object AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MAX_SIZE extends DataConstraintException("AUTHENTICATION_RESPONSE_DTO_REFRESH_TOKEN_MAX_SIZE")

case class AccessTokenDto(accessToken: String) {

  if (accessToken == null) throw ACCESS_TOKEN_DTO_ACCESS_TOKEN_IS_REQUIRED
  if (accessToken.size < 0) throw ACCESS_TOKEN_DTO_ACCESS_TOKEN_MIN_SIZE
  if (accessToken.size > 128) throw ACCESS_TOKEN_DTO_ACCESS_TOKEN_MAX_SIZE
}

object AccessTokenDto {
  val ACCESSTOKEN: String = "accessToken"
}

object ACCESS_TOKEN_DTO_ACCESS_TOKEN_IS_REQUIRED extends DataConstraintException("ACCESS_TOKEN_DTO_ACCESS_TOKEN_IS_REQUIRED")

object ACCESS_TOKEN_DTO_ACCESS_TOKEN_MIN_SIZE extends DataConstraintException("ACCESS_TOKEN_DTO_ACCESS_TOKEN_MIN_SIZE")

object ACCESS_TOKEN_DTO_ACCESS_TOKEN_MAX_SIZE extends DataConstraintException("ACCESS_TOKEN_DTO_ACCESS_TOKEN_MAX_SIZE")

case class RefreshTokenDto(refreshToken: String) {

  if (refreshToken == null) throw REFRESH_TOKEN_DTO_REFRESH_TOKEN_IS_REQUIRED
  if (refreshToken.size < 0) throw REFRESH_TOKEN_DTO_REFRESH_TOKEN_MIN_SIZE
  if (refreshToken.size > 128) throw REFRESH_TOKEN_DTO_REFRESH_TOKEN_MAX_SIZE
}

object RefreshTokenDto {
  val REFRESHTOKEN: String = "refreshToken"
}

object REFRESH_TOKEN_DTO_REFRESH_TOKEN_IS_REQUIRED extends DataConstraintException("REFRESH_TOKEN_DTO_REFRESH_TOKEN_IS_REQUIRED")

object REFRESH_TOKEN_DTO_REFRESH_TOKEN_MIN_SIZE extends DataConstraintException("REFRESH_TOKEN_DTO_REFRESH_TOKEN_MIN_SIZE")

object REFRESH_TOKEN_DTO_REFRESH_TOKEN_MAX_SIZE extends DataConstraintException("REFRESH_TOKEN_DTO_REFRESH_TOKEN_MAX_SIZE")
