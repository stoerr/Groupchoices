package com.tsmms.hackathon.choices

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.tsmms.hackathon.choices.miniwicket.{MiniWicketProcessor, MiniWicketServletFilter}

import scala.util.Random
import scala.xml.Text

import AbstractController._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 27.02.2015
 */
class ChoiceDispatcher extends HttpServlet {

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case NewPollController.path => response.sendRedirect(new NewPollController().processPost())
      case SaveVoteController.pathRegex(encodedId) =>
        response.sendRedirect(new SaveVoteController(decodeId(encodedId)).processPost())
    }
  }

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val implicitRequest = request
    request.getServletPath + Option(request.getPathInfo).getOrElse("") match {
      case "/new" => showPage("/newpoll.xhtml", request, response)
      case PollOverviewController.pathRegex(id) =>
        MiniWicketProcessor.addField("name", new Text("the name also"))
        MiniWicketProcessor.addField("description", new Text("make the description"))
        showPage("/polloverview.xhtml", request, response)
    }
  }

  def showPage(path: String, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    response.setDateHeader("Expires", 0)
    getServletContext.getContext("/").getRequestDispatcher(path).forward(request, response)
  }

}
