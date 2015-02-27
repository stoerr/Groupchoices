package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._

/** Provides some functionality to store in google datastore. This should work per annotations or reflection or
  * JPA or whatnot, but for now we just to it by hand.
  * We put that into the domain classes directly, so that we can't forget it when an attribute is added. */
trait DataStoreStorable {
  def copyToEntity(container: PropertyContainer)

  def toEmbeddedEntity = {
    val embeddedEntity = new EmbeddedEntity
    copyToEntity(embeddedEntity)
    embeddedEntity
  }

}

object DataStoreStorable {
  def readEmbeddedList[T](entity: PropertyContainer, propertyName: String, constructor: EmbeddedEntity => T) =
    entity.getProperty("propertyName").asInstanceOf[List[EmbeddedEntity]] map constructor
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
object PollDao {

  val pollEntityName = "Poll"

  val ds = DatastoreServiceFactory.getDatastoreService

  def makeKey(id: Long): Key = KeyFactory.createKey(pollEntityName, id)

  /** Saves object and returns it with initialized key. */
  def saveOrUpdate(obj: Poll): Poll = {
    val entity = if (obj.id.isEmpty) new Entity(pollEntityName) else ds.get(KeyFactory.createKey(pollEntityName, obj
      .id.get))
    obj.copyToEntity(entity)
    val savedKey = ds.put(entity)
    obj.copy(id = Some(savedKey.getId))
  }

  def get(id: Long): Option[Poll] = {
    try {
      Some(new Poll(ds.get(makeKey(id))))
    } catch {
      case _: EntityNotFoundException => None
    }
  }

  def findByAdminId(adminId: String): Option[Poll] = {
    val query = new Query(pollEntityName).setFilter(new FilterPredicate("adminId", FilterOperator.EQUAL, adminId))
    val entity = ds.prepare(query).asSingleEntity()
    Option(entity) map (new Poll(_))
  }


}
