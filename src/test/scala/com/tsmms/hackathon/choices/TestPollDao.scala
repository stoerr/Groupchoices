package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore._
import com.google.appengine.tools.development.testing.{LocalDatastoreServiceTestConfig, LocalServiceTestHelper}
import com.tsmms.hackathon.choices.PollDao._
import org.scalatest.{BeforeAndAfter, FlatSpec}

/**
 * Tries saving and reading something from the datastore.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 23.02.2015
 */
class TestPollDao extends FlatSpec with BeforeAndAfter {

  val helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())

  before {
    helper.setUp()
  }

  after {
    helper.tearDown()
  }

  val testpoll = Poll(id = None, adminId = "alskdjasd", name = "the name", description = "laber ga gum go",
    choices = List(Choice("hu", "ha"), Choice("foo", "bar")),
    votes = List(
      Vote("firstuser", ratings = List(Rating("hu", 7), Rating("ha", 4))),
      Vote("seconduser", ratings = List(Rating("hu", 2), Rating("ha", 8)))
    ))

  "Poll" should "transform to entities and back" in {
    val entity = new Entity(pollEntityName, 42L)
    testpoll.copyToEntity(entity)
    val readback: Poll = new Poll(entity)
    assert(testpoll.copy(id = Some(42)) == readback)
  }

  "PollDao" should "save and retrieve DsSaveEntities" in {

    val savedpoll = saveOrUpdate(testpoll)
    assert(null != savedpoll.id)
    val testpollWithId = testpoll.copy(id = savedpoll.id)
    assert(savedpoll == testpollWithId)
    assert(None == get(6661))
    assert(Some(savedpoll) == get(savedpoll.id.get))

    val changedPoll = savedpoll.copy(description = "Yo!")
    val changedSaved = saveOrUpdate(changedPoll)
    assert(Some(changedPoll) == get(changedPoll.id.get))

    assert(Some(changedPoll) == findByAdminId(testpoll.adminId))
    assert(None == findByAdminId("whatever"))
  }

}
