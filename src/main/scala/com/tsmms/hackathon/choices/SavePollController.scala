package com.tsmms.hackathon.choices

import java.util.Date
import java.util.concurrent.TimeUnit
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

  val expirationDate = new Date(System.currentTimeMillis() + 2629744L * request.getParameter("expirationMonths").toLong)

  val poll = Poll(id = None, adminId = Random.nextLong().toString, name = request.getParameter("name"), description =
    request.getParameter("description"), choices = choices.toList, expires = expirationDate)

  def processPost(): String = {
    val savedPoll = PollDao.saveOrUpdate(poll)
    PollOverviewController.path(savedPoll.id.get)
  }

}
