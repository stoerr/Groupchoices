package com.tsmms.hackathon.choices

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.tsmms.hackathon.choices.AbstractController._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class ChoiceDispatcher extends HttpServlet {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case SavePollController.path => response.sendRedirect(new SavePollController().processPost())
      case SaveVoteController.pathRegex(encodedId) =>
        response.sendRedirect(new SaveVoteController(decodeId(encodedId)).processPost())
    }
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case "/new" | "" | "/" => showPage("/createpoll.xhtml", request, response)
      case PollOverviewController.pathRegex(id) => new PollOverviewController(decodeId(id)).process()
        showPage("/polloverview.xhtml", request, response)
      case NewVoteController.pathRegex(id) => new NewVoteController(decodeId(id)).process()
        showPage("/createvote.xhtml", request, response)
    }
  }

  def showPage(path: String, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setDateHeader("Expires", 0)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1
    response.setHeader("Pragma", "no-cache") // HTTP 1.0
    response.setContentType("application/xhtml+xml")
    response.setCharacterEncoding("UTF-8")
    getServletContext.getContext("/").getRequestDispatcher(path).forward(request, response)
  }

}
