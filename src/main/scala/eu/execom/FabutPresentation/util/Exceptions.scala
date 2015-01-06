package eu.execom.FabutPresentation.util

trait ErrorException extends Throwable {

  def code: String

}

case class UnauthorizedException(code: String) extends ErrorException

case class BadRequestException(code: String) extends ErrorException

case class DataConstraintException(code: String) extends ErrorException
