package eu.execom.FabutPresentation.util

import org.slf4j.{Logger => Slf4jLogger}
import org.slf4j.LoggerFactory

trait Logger {

  protected val slf4jLogger: Slf4jLogger

  def error(msg: => String) {
    if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg)
  }

  def error(msg: => String, t: Throwable) {
    if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg, t)
  }

  def warn(msg: => String) {
    if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg)
  }

  def warn(msg: => String, t: Throwable) {
    if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg, t)
  }

  def info(msg: => String) {
    if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg)
  }

  def info(msg: => String, t: Throwable) {
    if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg, t)
  }

  def debug(msg: => String) {
    if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg)
  }

  def debug(msg: => String, t: Throwable) {
    if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg, t)
  }

  def trace(msg: => String) {
    if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg)
  }

  def trace(msg: => String, t: Throwable) {
    if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg, t)
  }
}

class DefaultLogger(protected val slf4jLogger: Slf4jLogger) extends Logger

trait Logging {
  protected lazy val logger = new DefaultLogger(LoggerFactory.getLogger(this.getClass))
}
