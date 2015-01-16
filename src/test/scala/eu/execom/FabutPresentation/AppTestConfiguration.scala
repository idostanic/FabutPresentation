package eu.execom.FabutPresentation

import java.util.Properties

import eu.execom.FabutPresentation.api._

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

}
