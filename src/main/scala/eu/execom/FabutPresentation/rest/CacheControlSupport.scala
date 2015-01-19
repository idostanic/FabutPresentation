package eu.execom.FabutPresentation.rest

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

trait CacheControlSupport {

  implicit def request: HttpServletRequest

  implicit def response: HttpServletResponse

  val RFC1123_PATTERN = "EEE, dd MMM yyyyy HH:mm:ss z"
  val dateTimeFormat = DateTimeFormat.forPattern(RFC1123_PATTERN)

  def noCache() = {
    response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate")
    response.addHeader("Pragma", "no-cache")
  }

  def maxAge(seconds: Long) = {
    response.addHeader("Cache-Control", "max-age=" + seconds)
  }

  def expires(expiration: DateTime) = {
    response.addHeader("Expires", dateTimeFormat.print(expiration))
  }

}
