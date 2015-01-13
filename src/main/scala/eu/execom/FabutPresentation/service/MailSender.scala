package eu.execom.FabutPresentation.service

import org.apache.commons.mail.HtmlEmail
import scala.util.Try
import eu.execom.FabutPresentation.util.Logging
import eu.execom.FabutPresentation.persistence.User

class MailSender(
  val noFacebookEmail: String,
  val feedbackEmail: String,
  val smtpUrl: String,
  val smtpPort: Int,
  val smtpUserName: String,
  val smtpPassword: String,
  val smtpSslOnConnect: Boolean) extends Logging {
  require(smtpUserName.nonEmpty, "SMTP user name can't be empty")

  def sendEmail(toAddress: String, toName: Option[String], fromAddress: String, fromName: String, subject: String, content: String): Unit = {

    val email = new HtmlEmail()
    email.setHostName(smtpUrl)
    email.setSmtpPort(smtpPort)
    if (smtpUserName.nonEmpty && smtpPassword.nonEmpty) email.setAuthentication(smtpUserName, smtpPassword)
    email.setSSLOnConnect(smtpSslOnConnect)
    email.addTo(toAddress)
    email.setFrom(fromAddress, "")
    email.setSubject(subject)
    email.setCharset("UTF-8")
    email.setHtmlMsg(content)
    email.setTextMsg("Your email client does not support HTML messages")
    email.send()

  }

  def sendEmail(toAddress: String, toName: Option[String]) {
    sendEmail(toAddress, toName, smtpUserName, "FabutPresentation", "FABUT", "PRESENTATION")
  }

  def sendInvitationEmail(user: User, email: String) {

    sendEmail(email, None)

  }
}
