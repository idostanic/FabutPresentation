package eu.execom.FabutPresentation.persistence

trait Enum {

  def name: String

}

case class SortOrder(name: String) extends Enum

object SortOrder {
  val ASC = SortOrder("ASC")
  val DESC = SortOrder("DESC")
  val values: List[SortOrder] = ASC :: DESC :: Nil

  def withName(name:String):SortOrder = values.find(_.name == name).get
}

case class InvitationStatus(name: String) extends Enum

object InvitationStatus {
  val PENDING = InvitationStatus("PENDING")
  val USED = InvitationStatus("USED")
  val values: List[InvitationStatus] = PENDING :: USED :: Nil

  def withName(name:String):InvitationStatus = values.find(_.name == name).get
}

case class FriendRequestStatus(name: String) extends Enum

object FriendRequestStatus {
  val PENDING = FriendRequestStatus("PENDING")
  val BEFRENDED = FriendRequestStatus("BEFRENDED")
  val values: List[FriendRequestStatus] = PENDING :: BEFRENDED :: Nil

  def withName(name:String):FriendRequestStatus = values.find(_.name == name).get
}
