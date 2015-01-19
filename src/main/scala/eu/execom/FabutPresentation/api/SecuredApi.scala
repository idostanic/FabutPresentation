package eu.execom.FabutPresentation.api

import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.service._

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}

trait SecuredApi {

  val securedService: SecuredService

  def secure[T](authCode: String)(f: User => T)(implicit slickSession: SlickSession): T = {
    val (user, client) = securedService.authenticate(authCode).getOrElse(throw CREDENTIALS_ARE_INVALID)

    if (client.accessTokenExpires.isAfterNow) throw ACCESS_TOKEN_IS_EXPIRED

    f(user)
  }

}
