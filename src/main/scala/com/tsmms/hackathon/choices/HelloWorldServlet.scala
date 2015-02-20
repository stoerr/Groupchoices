package com.tsmms.hackathon.choices

import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 20.02.2015
 */
class HelloWorldServlet extends HttpServlet {

  val logger = Logger.getLogger(getClass.toString)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1
    resp.setHeader("Pragma", "no-cache") // HTTP 1.0
    resp.setDateHeader("Expires", 0)
    req.setAttribute("title", "Hello world page")
    req.setAttribute("heading", "Hello world!")
    req.setAttribute("body", <p>Some body, hu hu hu</p>)
    getServletContext.getRequestDispatcher("/jsp/frame.jsp").forward(req, resp)
  }

}
