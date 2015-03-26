package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import com.tsmms.hackathon.choices.AbstractController._
import com.tsmms.hackathon.choices.miniwicket.MiniWicketProcessor._

object NewVoteController {
  def path(id: Long) = "/c/" + encodeId(id) + "/new"

  val pathRegex = "/c/(-?[0-9a-z]+)/new".r
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 05.03.2015
 */
class NewVoteController(id: Long)(implicit request: HttpServletRequest) extends AbstractController(request) {

  val poll = PollDao.get(id).get

  def process(): Unit = {
    addAttribute("voteform", "action", SaveVoteController.path(id))
    addRepeater("choicerow", poll.choices.zipWithIndex map { case (choice, idx) => () =>
      addField("choice", choice.name)
      addAttribute("vote", "name", "choice" + idx)
      addAttribute("choicegroup", "id", "choice" + idx)
      addRepeater("votelabel", (0 to 10) map { rating => () =>
        addAttribute("vote", "value", rating.toString)
        addField("rating", rating.toString)
      })
    })
  }

}
