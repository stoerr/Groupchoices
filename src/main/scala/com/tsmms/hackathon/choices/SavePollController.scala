package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import scala.collection.JavaConversions._
import scala.util.Random

object SavePollController {
  val path = "/a"
}

/**
 * Saves a new poll
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.201
 */
class SavePollController(implicit request: HttpServletRequest) extends AbstractController(request) {

  val choices = choiceParameters map (name => new Choice(AbstractController.makeRandomEncodedId(), name))

  val poll = Poll(id = None, adminId = Random.nextLong().toString, name = request.getParameter("name"), description =
    request.getParameter("description"), choices = choices.toList)

  def processPost(): String = {
    val savedPoll = PollDao.saveOrUpdate(poll)
    PollOverviewController.path(savedPoll.id.get)
  }

}
