package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import com.tsmms.hackathon.choices.AbstractController._
import com.tsmms.hackathon.choices.miniwicket.MiniWicketProcessor._

object SaveVoteController {
  def path(id: Long) = "/c/" + encodeId(id) + "/v/"

  val pathRegex = "/c/(-?[0-9a-z]+)/v/".r
}

/**
 * Saves the ratings of a user.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 03.03.2015
 */
class SaveVoteController(id: Long)(implicit request: HttpServletRequest) extends AbstractController(request) {

  val poll = PollDao.get(id).get
  val vote = Vote(id = makeRandomEncodedId(), username = parameter("username"),
    (poll.choices, choiceParameters).zipped.map((choice, param) => Rating(choice.id, param.toInt)))

  require(poll.choices.size == choiceParameters.size, "Received not exactly as many poll answers as poll choices.")

  val savedPoll = PollDao.saveOrUpdate(poll.copy(votes = poll.votes :+ vote))

  def processPost(): String = {
    return PollOverviewController.path(savedPoll.id.get)
  }

}
