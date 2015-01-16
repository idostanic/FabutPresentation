package eu.execom.FabutPresentation.rest

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util._
import org.joda.time.DateTime
import org.json4s._
import org.scalatra.GZipSupport
import org.scalatra.json._

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}
import scala.slick.jdbc.JdbcBackend.Database

object JSON_REQUEST_REQUIRED_EXCEPTION extends BadRequestException("JSON_REQUEST_REQUIRED_EXCEPTION")

class HttpApi(val slickDb: Database, val authenticationApi: AuthenticationApi) extends AbstractSecuredServlet with ApiCaller with JacksonJsonSupport with GZipSupport with CacheControlSupport {

  before() {
    contentType = formats("json")
  }

  post("/signOut") {
    transaction  { (slickSession: SlickSession) =>
      logger.trace("Rest url: /signOut type: POST")

      val authenticationCode: String = securityToken

      val response = authenticationApi.signOut(authenticationCode)(slickSession).get
      logger.trace(s"Response: $response")
      securityToken = ""
      response
    }
  }

  post("/authenticate") {
    transaction  { (slickSession: SlickSession) =>
      logger.trace("Rest url: /authenticate type: POST")

      if (requestFormat !="json") throw JSON_REQUEST_REQUIRED_EXCEPTION
      val body:AccessTokenDto = parsedBody match {
        case JNothing => throw JSON_REQUEST_REQUIRED_EXCEPTION
        case jsonBody => jsonBody.extract[AccessTokenDto]
      }
      logger.trace("Body:" + body)

      val response = authenticationApi.authenticate(body)(slickSession).get
      logger.trace(s"Response: $response")
      response
    }
  }

  post("/refreshToken") {
    transaction  { (slickSession: SlickSession) =>
      logger.trace("Rest url: /refreshToken type: POST")

      if (requestFormat !="json") throw JSON_REQUEST_REQUIRED_EXCEPTION
      val body:RefreshTokenDto = parsedBody match {
        case JNothing => throw JSON_REQUEST_REQUIRED_EXCEPTION
        case jsonBody => jsonBody.extract[RefreshTokenDto]
      }
      logger.trace("Body:" + body)

      val response = authenticationApi.refreshToken(body)(slickSession).get
      logger.trace(s"Response: $response")
      securityToken = response.accessToken
      response
    }
  }
}
