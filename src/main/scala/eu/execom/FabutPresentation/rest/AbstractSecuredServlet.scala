package eu.execom.FabutPresentation.rest

import java.util._

import eu.execom.FabutPresentation.api._
import eu.execom.FabutPresentation.persistence._
import eu.execom.FabutPresentation.util._
import org.joda.time.DateTime
import org.json4s._
import org.scalatra._
import sun.misc.BASE64Encoder

abstract class AbstractSecuredServlet extends ScalatraServlet with Logging {

  val SECURITY_TOKEN = "X-FABUTPRESENTATION-AUTH"

  protected implicit val jsonFormats: Formats = DefaultFormats + new ArrayByteSerializer + DateTimeSerializer + new UUIDSerializer + new SortOrderSerializer + new InvitationStatusSerializer + new FriendRequestStatusSerializer

  error {
    case BadRequestException(code) =>
      logger.trace("BadRequest error occurred with code: " + code)
      halt(BadRequest(body = errorJson(code, "BadRequest error occurred with code")))
    case DataConstraintException(code) =>
      logger.trace("Data constraint error occurred with code: " + code)
      halt(BadRequest(body = errorJson(code, "Data constraint error occurred with code")))
    case UnauthorizedException(code) =>
      logger.trace("Unauthorized error occurred with code: " + code)
      halt(Unauthorized(body = errorJson(code, "Unauthorized error occurred with code")))
    case e:MappingException =>
      logger.warn(s"Bad data request, json object invalid format, ${e.msg}")
      halt(BadRequest(body = errorJson("JSON_MAPPING_EXCEPTION", s"Bad data request, json object invalid format, ${e.msg}")))
    case e =>
      logger.error("Unknown error occurred", e)
      halt(InternalServerError(reason = e.toString))
  }

  def errorJson(code:String, message: String) = {
    s"""{ "code": "$code", "message": "$message" }""".stripMargin
  }

  def securityToken: String = (cookies.get(SECURITY_TOKEN), request.header(SECURITY_TOKEN)) match {
    case (Some(authCode), _) =>
      logger.trace("Requested security token is found in cookie")
      authCode
    case (_, Some(authCode)) =>
      logger.trace("Requested security token is found in header cookie")
      authCode
    case _ =>
      logger.error("Security token doesn't exist")
      halt(Unauthorized(reason = "USER_CREDENTIALS_ARE_INVALID"))
  }

  def securityToken_=(token: String): Unit = cookies.set(SECURITY_TOKEN, token)

}

class ArrayByteSerializer extends Serializer[Array[Byte]] {
  private val MyClassClass = classOf[Array[Byte]]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Array[Byte]] = {
    case (TypeInfo(MyClassClass, _), json) => json match {
      case JString(content) =>
        new sun.misc.BASE64Decoder().decodeBuffer(content)
      case x => throw new MappingException("Can't convert " + x + " to Array[Byte]")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: Array[Byte] => JString(new BASE64Encoder().encode(x))
  }
}

case object DateTimeSerializer extends CustomSerializer[DateTime](format => ( {
  case JString(s) =>
    val milliseconds: Long = format.dateFormat.parse(s).map(_.getTime).getOrElse(throw new MappingException("Invalid date format " + s))
    new DateTime(milliseconds)
}, {
  case d: DateTime => JString(format.dateFormat.format(d.toDate))
}
  ))

class UUIDSerializer extends Serializer[UUID] {
  private val MyClassClass = classOf[UUID]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
    case (TypeInfo(MyClassClass, _), json) => json match {
      case JString(content) => UUID.fromString(content)
      case x => throw new MappingException("Can't convert " + x + " to UUID")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: UUID => JString(x.toString)
  }
}

class SortOrderSerializer extends Serializer[SortOrder] {
  private val MyClassClass = classOf[SortOrder]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), SortOrder] = {
    case (TypeInfo(MyClassClass, _), json) => json match {
      case JString(content) => SortOrder.withName(content)
      case x => throw new MappingException("Can't deserialize " + x + " to SortOrder")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: SortOrder => JString(x.name)
  }
}

class InvitationStatusSerializer extends Serializer[InvitationStatus] {
  private val MyClassClass = classOf[InvitationStatus]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), InvitationStatus] = {
    case (TypeInfo(MyClassClass, _), json) => json match {
      case JString(content) => InvitationStatus.withName(content)
      case x => throw new MappingException("Can't deserialize " + x + " to InvitationStatus")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: InvitationStatus => JString(x.name)
  }
}

class FriendRequestStatusSerializer extends Serializer[FriendRequestStatus] {
  private val MyClassClass = classOf[FriendRequestStatus]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), FriendRequestStatus] = {
    case (TypeInfo(MyClassClass, _), json) => json match {
      case JString(content) => FriendRequestStatus.withName(content)
      case x => throw new MappingException("Can't deserialize " + x + " to FriendRequestStatus")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: FriendRequestStatus => JString(x.name)
  }
}
