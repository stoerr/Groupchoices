package com.tsmms.hackathon.choices

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.util.Random

object ChoiceDispatcher {
  def encodeId(id: Long) = java.lang.Long.toString(id, Character.MAX_RADIX)

  def decodeId(encodedId: String) = java.lang.Long.parseLong(encodedId, Character.MAX_RADIX)

  def makeRandomEncodedId(): String = encodeId(Random.nextLong())
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class ChoiceDispatcher extends HttpServlet {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case SavePollController.path => response.sendRedirect(new SavePollController().processPost())
    }
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case "/new" => showPage("/newpoll.xhtml", request, response)
      case PollOverviewController.pathRegex(id) => new PollOverviewController(id).process()
        showPage("/polloverview.xhtml", request, response)
    }
  }

  def showPage(path: String, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setDateHeader("Expires", 0)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1
    response.setHeader("Pragma", "no-cache") // HTTP 1.0
    getServletContext.getContext("/").getRequestDispatcher(path).forward(request, response)
  }

}
