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

  // println("Overview : " + poll)

  def process() = {
    addField("name", poll.name)
    addField("description", poll.description)
    addField("usercount", poll.votes.size.toString)
    addAttribute("voteform", "action", NewVoteController.path(id))

    val usernames = poll.votes.map(_.username)
    addField("polllink", request.getRequestURL.toString)
    addAttribute("polllinkhref", "href", request.getRequestURL.toString)

    addRepeater("usernamerepeat", usernames map { name => () =>
      addField("username", name)
    })
    val choices = poll.choices.map(_.name)
    val userratings = poll.votes.map(_.ratings.map(_.rating))
    val userratingsTransposed = if (poll.votes.size > 0) userratings.transpose else List.fill(choices.size)(List
      .empty[Int])
    addRepeater("usertablerow", choices.zip(userratingsTransposed) map { case (choice, ratingsrow) => () =>
      addField("choice", choice)
      addRepeater("voterepeat", ratingsrow map { rating => () =>
        addField("vote", rating.toString)
      })
    })

    val choiceTablePresent = if (0 < poll.votes.length) Some(() => ()) else None
    addRepeater("choicetable", choiceTablePresent.toList)

    val averageRatings = userratingsTransposed.map(row => row.sum * 1.0 / row.length)
    addRepeater("choicetablerow", choices.zip(averageRatings) map { case (choice, avgrating) => () =>
      addField("choice", choice)
      addField("avgrating", avgrating.toFloat.toString)
    })

  }

}
