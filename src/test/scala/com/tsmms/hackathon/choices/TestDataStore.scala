package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore.FetchOptions.Builder._
import com.google.appengine.api.datastore.Query.{FilterOperator, FilterPredicate}
import com.google.appengine.api.datastore._
import com.google.appengine.tools.development.testing.{LocalDatastoreServiceTestConfig, LocalServiceTestHelper}
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
 * Tries saving and reading something from the datastore.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 23.02.2015
 */
class TestDataStore extends FlatSpec with BeforeAndAfter {

  val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())

  before {
    helper.setUp()
  }

  after {
    helper.tearDown()
  }

  "the datastore" should "saveOrUpdate and retrieve entities" in {
    val ds = DatastoreServiceFactory.getDatastoreService
    assert(0 == ds.prepare(new Query("yam")).countEntities(withLimit(10)))
    ds.put(new Entity("yam"))
    ds.put(new Entity("yam"))
    assert(2 == ds.prepare(new Query("yam")).countEntities(withLimit(10)))
  }

  case class DsSaveEntity(key: Key, value: Int, otheridx: String) {
    def this(entity: Entity) = this(key = entity.getKey, value = entity.getProperty("value").asInstanceOf[java.lang.Long].intValue(),
      otheridx = entity.getProperty("otheridx").asInstanceOf[String])

    def updateEntity(entity: Entity) = {
      entity.setProperty("value", value)
      entity.setProperty("otheridx", otheridx)
      entity
    }
  }

  object DsSaveEntityDao {
    val ds = DatastoreServiceFactory.getDatastoreService
    val dsSaveEntityName = "dssaveentity"

    /** Saves object and returns it with initialized key. */
    def saveOrUpdate(obj: DsSaveEntity): DsSaveEntity = {
      val entity = if (null == obj.key) new Entity(dsSaveEntityName) else ds.get(obj.key)
      obj.updateEntity(entity)
      val savedKey = ds.put(entity)
      obj.copy(key = savedKey)
    }

    def get(key: Key): Option[DsSaveEntity] = {
      try {
        Some(new DsSaveEntity(ds.get(key)))
      } catch {
        case _: EntityNotFoundException => None
      }
    }

    def findByOtherIdx(otheridx: String): Option[DsSaveEntity] = {
      val query = new Query(dsSaveEntityName).setFilter(new FilterPredicate("otheridx", FilterOperator.EQUAL, otheridx))
      val entity = ds.prepare(query).asSingleEntity()
      Option(entity) map (new DsSaveEntity(_))
    }
  }

  "DsSaveEntityDao" should "save and retrieve DsSaveEntities" in {
    import DsSaveEntityDao._
    val e1 = saveOrUpdate(DsSaveEntity(value = 1, otheridx = "fkey1", key = null))
    assert(1 == e1.value && "fkey1" == e1.otheridx && null != e1.key)
    val e2 = saveOrUpdate(DsSaveEntity(value = 2, otheridx = "fkey2", key = null))
    assert(2 == e2.value && "fkey2" == e2.otheridx && null != e2.key)
    assert(Some(e1) == get(e1.key) && Some(e2) == get(e2.key))
    assert(Some(e1) == findByOtherIdx(e1.otheridx) && Some(e2) == findByOtherIdx(e2.otheridx))
    assert(None == get(KeyFactory.createKey(dsSaveEntityName, 666l)))
    val e1chg = e1.copy(value = 17)
    val e1chgback = saveOrUpdate(e1chg)
    assert(Some(e1chg) == get(e1.key))
    assert(Some(e1) != get(e1.key))
  }

}
