package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import scala.collection.JavaConversions._
import scala.util.Random

object NewPollController {
  val path = "/a"
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class NewPollController(request: HttpServletRequest) {

  val allParameters = request.getParameterMap.toArray.asInstanceOf[Array[(String, Array[String])]]

  val choices = allParameters filter (_._1.startsWith("choice")) map { case (key, values) =>
    (key.substring("choice".length).toInt, values.head)
  } sortBy (_._1) map (entry => new Choice(ChoiceDispatcher.makeRandomEncodedId(), entry._2))

  val poll = Poll(id = None, adminId = Random.nextLong().toString, name = request.getParameter("name"), description =
    request.getParameter("description"), choices = choices.toList)

  def processPost(): String = {
    val savedPoll = PollDao.saveOrUpdate(poll)
    PollOverviewController.path(savedPoll.id.get)
  }

}
