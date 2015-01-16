package eu.execom.FabutPresentation.persistence

import eu.execom.FabutPresentation.util.Logging

import scala.slick.jdbc.JdbcBackend.{Session => SlickSession}
import scala.slick.lifted._

abstract class GenericSlickDao[T] extends Logging {

  def save(entity: T)(implicit session: SlickSession): Unit

  def update(entity: T)(implicit session: SlickSession): Unit

  def sort[T](column: Column[T], order: SortOrder): ColumnOrdered[T] = order match {
    case SortOrder.ASC => column.asc.nullsFirst
    case SortOrder.DESC => column.desc.nullsFirst
  }

}
