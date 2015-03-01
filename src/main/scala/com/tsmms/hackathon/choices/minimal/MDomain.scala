package com.tsmms.hackathon.choices.minimal

import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._
import org.json4s.native.Serialization

/** Contains all data about a poll - incl. answers by all users. */
case class MPoll(
                  name: String,
                  description: String,
                  choices: List[String] = List.empty,
                  votes: List[MVote] = List.empty,
                  id: Option[Long] = None,
                  adminId: String = MChoiceController.makeStringId
                  )

case class MVote(
                  username: String,
                  ratings: List[Int]
                  )

object MPollDao {

  import org.json4s._
  import org.json4s.native.Serialization
  import org.json4s.native.Serialization.{read, write}

  implicit val formats = Serialization.formats(NoTypeHints)
  // scala> val ser = write(Child("Mary", 5, None))
  // scala> read[Child](ser)

  val pollEntityName = "MPoll"

  val ds = DatastoreServiceFactory.getDatastoreService

  def saveOrUpdate(poll: MPoll): MPoll = {
    val entity = if (poll.id.isDefined) new Entity(pollEntityName, poll.id.get) else new Entity(pollEntityName)
    entity.setProperty("adminId", poll.adminId)
    entity.setUnindexedProperty("pickled", new Text(write(poll)))
    val key = ds.put(entity)
    poll.copy(id = Some(key.getId))
  }

  private def decode(entity: Entity): MPoll =
    read[MPoll](entity.getProperty("pickled").asInstanceOf[Text].getValue).copy(id = Some(entity.getKey.getId))

  def get(id: Long): Option[MPoll] = {
    try {
      val entity = ds.get(KeyFactory.createKey(pollEntityName, id))
      Some(decode(entity))
    } catch {
      case _: EntityNotFoundException => return None
    }
  }

  def findByAdminId(adminId: String): Option[MPoll] = {
    val query = new Query(pollEntityName).setFilter(new FilterPredicate("adminId", FilterOperator.EQUAL, adminId))
    val entity = ds.prepare(query).asSingleEntity()
    Option(entity) map (decode)
  }

}
