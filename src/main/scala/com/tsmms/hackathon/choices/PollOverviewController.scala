package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import com.tsmms.hackathon.choices.miniwicket.MiniWicketProcessor._

object PollOverviewController {
  def path(id: Long) = "/a/" + ChoiceDispatcher.encodeId(id)

  val pathRegex = "/a/(-?[0-9a-z]+)".r
}

/**
 * Display the current votes for all choices and the results.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class PollOverviewController(id: String)(implicit request: HttpServletRequest) {

  def process() = {
    addField("name", "The name of the poll")
    addField("description", "Describe describe, describe too.")
    val usernames = List("User A", "User B")
    addRepeater("username", usernames map { name => () =>
      addField("username", name)
    })
    val choices = List("Choice Eins", "Choice 2")
    val userratingsTransposed = List(List(1, 4), List(3, 7))
    addRepeater("usertablerow", choices.zip(userratingsTransposed) map { case (choice, ratingsrow) => () =>
      addField("choice", choice)
      addRepeater("vote", ratingsrow map { rating => () =>
        addField("vote", rating.toString)
      })
    })
  }

}
