package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import com.tsmms.hackathon.choices.miniwicket.MiniWicketProcessor._

object PollOverviewController {
  def path(id: Long) = "/c/" + AbstractController.encodeId(id)

  val pathRegex = "/c/(-?[0-9a-z]+)".r
}

/**
 * Display the current votes for all choices and the results.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class PollOverviewController(id: Long)(implicit request: HttpServletRequest) extends AbstractController(request) {

  val poll = PollDao.get(id).get

  println("Overview : " + poll)

  def process() = {
    addField("name", poll.name)
    addField("description", poll.description)
    addAttribute("voteform", "action", NewVoteController.path(id))
    val usernames = poll.votes.map(_.username)
    addRepeater("username", usernames map { name => () =>
      addField("username", name)
    })
    val choices = poll.choices.map(_.name)
    val userratings = poll.votes.map(_.ratings.map(_.rating.toString)).transpose
    // val userratingsTransposed = if (poll.votes.size > 0) userratings.transpose else List.fill(choices.size)(List.empty[String])
    addRepeater("usertablerow", choices.zip(userratings) map { case (choice, ratingsrow) => () =>
      addField("choice", choice)
      addRepeater("vote", ratingsrow map { rating => () =>
        addField("vote", rating)
      })
    })
  }

}
