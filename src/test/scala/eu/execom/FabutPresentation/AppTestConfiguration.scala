package eu.execom.FabutPresentation

import java.util.Properties
import eu.execom.FabutPresentation.api._
import org.subethamail.wiser.Wiser

object AppTestConfiguration extends ApiConfiguration {

  lazy val testPropertyFileName = "/app_test.properties"

  lazy val properties: Properties = {
    val properties = new Properties()
    properties.load(getClass.getResourceAsStream(testPropertyFileName))
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
  lazy val appEmail: String = properties.getProperty("app.email")
  lazy val appName: String = properties.getProperty("app.name")
  lazy val appUrl: String = properties.getProperty("app.url")
  lazy val smtpUrl: String = properties.getProperty("smtp.url")
  lazy val smtpPort: Int = properties.getProperty("smtp.port").toInt
  lazy val smtpUserName: String = properties.getProperty("smtp.username")
  lazy val smtpPassword: String = properties.getProperty("smtp.password")
  lazy val smtpSslOnConnect: Boolean = properties.getProperty("smtp.sslonconnect").toBoolean


  //start mock SMTP
  val wiser = new Wiser()
  wiser.setHostname(smtpUrl)
  wiser.setPort(smtpPort)
  wiser.start()

}
