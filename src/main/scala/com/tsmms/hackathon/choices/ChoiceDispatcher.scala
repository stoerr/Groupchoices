package com.tsmms.hackathon.choices

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class ChoiceDispatcher extends HttpServlet {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case NewPollController.path => response.sendRedirect(new NewPollController(request).processPost())
    }
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case "/new" => showPage("/makevote.html", request, response)
      case PollOverviewController.pathRegex(id) => showPage("/pollOverview.html", request, response)
    }
  }

  def showPage(path: String, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setDateHeader("Expires", 0)
    getServletContext.getRequestDispatcher(path).forward(request, response)
  }

}
