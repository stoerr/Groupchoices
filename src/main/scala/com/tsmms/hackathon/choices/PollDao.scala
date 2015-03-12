package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._
import com.tsmms.hackathon.choicesprototype.MPoll

import scala.collection.JavaConversions._

/**
 * Stores the polls as json - we only have the key and adminId as actual datastore properties to save
 * free quota.
 *
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
object PollDao {

  import org.json4s._
  import org.json4s.native.Serialization
  import org.json4s.native.Serialization.{read, write}

  implicit val formats = Serialization.formats(NoTypeHints)

  val pollEntityName = "Poll"

  val ds = DatastoreServiceFactory.getDatastoreService

  def makeKey(id: Long): Key = KeyFactory.createKey(pollEntityName, id)

  /** Saves object and returns it with initialized key. */
  def saveOrUpdate(obj: Poll): Poll = {
    val entity = if (obj.id.isEmpty) new Entity(pollEntityName)
    else ds.get(KeyFactory.createKey(pollEntityName, obj
      .id.get))
    entity.setProperty("adminId", obj.adminId)
    entity.setUnindexedProperty("json", new Text(write(obj)))
    val savedKey = ds.put(entity)
    obj.copy(id = Some(savedKey.getId))
  }

  private def decode(entity: Entity): Poll =
    read[Poll](entity.getProperty("json").asInstanceOf[Text].getValue).copy(id = Some(entity.getKey.getId))

  def get(id: Long): Option[Poll] = {
    try {
      val entity = ds.get(makeKey(id))
      Some(decode(entity))
    } catch {
      case _: EntityNotFoundException => None
    }
  }

  def findByAdminId(adminId: String): Option[Poll] = {
    val query = new Query(pollEntityName).setFilter(new FilterPredicate("adminId", FilterOperator.EQUAL, adminId))
    val entity = ds.prepare(query).asSingleEntity()
    Option(entity) map (decode)
  }


}
