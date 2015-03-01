package com.tsmms.hackathon.choices.minimal

import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._

/** Contains all data about a poll - incl. answers by all users. */
case class MPoll(
                  name: String,
                  description: String,
                  choices: List[MChoice] = List.empty,
                  votes: List[MVote] = List.empty,
                  id: Option[Long] = None,
                  adminId: String = MChoiceController.makeStringId
                  )

case class MChoice(
                    name: String
                    )

case class MVote(
                  username: String,
                  ratings: List[MRating]
                  )

case class MRating(
                    /** Rating between 0-9 */
                    rating: Int
                    )

object MPollDao {

  import scala.pickling._
  import scala.pickling.json._

  val pollEntityName = "MPoll"

  val ds = DatastoreServiceFactory.getDatastoreService

  def saveOrUpdate(poll: MPoll): MPoll = {
    val entity = if (poll.id.isDefined) new Entity(pollEntityName, poll.id.get) else new Entity(pollEntityName)
    entity.setProperty("adminId", poll.adminId)
    entity.setUnindexedProperty("pickled", new Text(poll.pickle.value))
    val key = ds.put(entity)
    poll.copy(id = Some(key.getId))
  }

  private def decode(entity: Entity): MPoll =
    entity.getProperty("pickled").asInstanceOf[Text].getValue.unpickle[MPoll].copy(id = Some(entity.getKey.getId))

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
