package eu.execom.FabutPresentation

import java.io.FileInputStream
import java.util.Properties
import javax.servlet.ServletContext

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import eu.execom.FabutPresentation.api._
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

class AppConfiguration extends ApiConfiguration {

  val logConfigurationFile = System.getenv("FABUTPRESENTATION_HOME") + "/logback.xml"
  val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  val jc: JoranConfigurator = new JoranConfigurator()
  context.reset()
  context.putProperty("application-name", "FabutPresentation")
  jc.setContext(context)
  jc.doConfigure(logConfigurationFile)

  lazy val appPropertyFilePath = System.getenv("FABUTPRESENTATION_HOME") + "/app.properties"
  lazy val properties: Properties = {
    val properties = new Properties()
    properties.load(new FileInputStream(appPropertyFilePath))
    properties
  }

  lazy val mysqlUsername: String = properties.getProperty("mysql.username")
  lazy val mysqlPassword: String = properties.getProperty("mysql.password")
  lazy val mysqlDatabaseName: String = properties.getProperty("mysql.database.name")
  lazy val mysqlServerName: String = properties.getProperty("mysql.server.name")
  lazy val mysqlCachePrepStmts: Boolean = properties.getProperty("mysql.cachePrepStmts").toBoolean
  lazy val mysqlPrepStmtCacheSize: Int = properties.getProperty("mysql.prepStmtCacheSize").toInt
  lazy val mysqlPrepStmtCacheSqlLimit: Int = properties.getProperty("mysql.prepStmtCacheSqlLimit").toInt
  lazy val mysqlUseServerPrepStmts: Boolean = properties.getProperty("mysql.useServerPrepStmts").toBoolean
  lazy val feedbackEmail: String = properties.getProperty("feedback.email")
  lazy val noFacebookEmail: String = properties.getProperty("no.facebook.email")
  lazy val smtpUrl: String = properties.getProperty("smtp.url")
  lazy val smtpPort: Int = properties.getProperty("smtp.port").toInt
  lazy val smtpUserName: String = properties.getProperty("smtp.username")
  lazy val smtpPassword: String = properties.getProperty("smtp.password")
  lazy val smtpSslOnConnect: Boolean = properties.getProperty("smtp.sslonconnect").toBoolean

  def initialize(): Unit = {
    //TODO do initialization
  }

  def cleanup(): Unit = {
    //TODO do cleanup
  }

}
