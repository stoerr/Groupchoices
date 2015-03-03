package com.tsmms.hackathon.choices

object PollOverviewController {
  def path(id: Long) = "/a/" + AbstractController.encodeId(id)

  val pathRegex = "/a/(-?[0-9a-z]+)".r
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class PollOverviewController {

}
