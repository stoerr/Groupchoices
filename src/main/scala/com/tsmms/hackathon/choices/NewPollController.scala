package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import scala.util.Random

object NewPollController {
  val path = "/a"
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class NewPollController(request: HttpServletRequest) {

  def processPost(): String = {
    val id = Random.nextLong().toString
    PollOverviewController.path(id)
  }

}
