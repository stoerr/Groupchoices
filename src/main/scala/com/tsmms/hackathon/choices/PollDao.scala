package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._

import scala.collection.JavaConversions._

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

  /** Abuse of embedded entities as list: we have properties item number with the actual values */
  def listToEmbeddedEntity[T](values: List[T]): EmbeddedEntity = {
    val entity = new EmbeddedEntity
    values.zipWithIndex.map { case (value, idx) =>
      entity.setProperty(idx.toString, value)
    }
    entity
  }
}

object DataStoreStorable {
  def readEmbeddedList[T](entity: PropertyContainer, propertyName: String, constructor: EmbeddedEntity => T): List[T]
  = {
    val thelistAsEntity = entity.getProperty(propertyName).asInstanceOf[EmbeddedEntity]
    thelistAsEntity.getProperties.toArray map { case (key, value) =>
      (key.toInt, value.asInstanceOf[EmbeddedEntity])
    } sortBy (_._1) map (_._2) map constructor toList
  }
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
    val entity = if (obj.id.isEmpty) new Entity(pollEntityName)
    else ds.get(KeyFactory.createKey(pollEntityName, obj
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
