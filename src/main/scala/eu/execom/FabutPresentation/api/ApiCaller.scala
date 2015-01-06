package eu.execom.FabutPresentation.api

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}
import scala.slick.jdbc.JdbcBackend.Database
import scala.util.{Success, Failure, Try}

trait ApiCaller {
  def slickDb: Database

  def securityToken: String
  def securityToken_=(token: String)

  def session[T](f: SlickSession => T): T = slickDb.withSession(slickSession => {
    
    f(slickSession)
  })

  def transaction[T](f: SlickSession => T): T = slickDb.withSession(slickSession => {
    
    Try(f(slickSession)) match {
      case Failure(e) =>
      throw e
    case Success(result) =>
      result
    }
  })
}
