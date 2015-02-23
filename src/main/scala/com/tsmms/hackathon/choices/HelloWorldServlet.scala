package com.tsmms.hackathon.choices

import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import com.google.appengine.api.datastore.{Entity, Query, DatastoreServiceFactory, DatastoreService}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 20.02.2015
 */
class HelloWorldServlet extends HttpServlet {

  val logger = Logger.getLogger(getClass.toString)

  val ds = DatastoreServiceFactory.getDatastoreService

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1
    resp.setHeader("Pragma", "no-cache") // HTTP 1.0
    resp.setDateHeader("Expires", 0)
    req.setAttribute("title", "Hello world page")
    req.setAttribute("heading", "Hello world!")
    req.setAttribute("body", <p>We have
      {callCount}
      calls</p>)
    getServletContext.getRequestDispatcher("/jsp/frame.jsp").forward(req, resp)
  }

  def callCount: Int = {
    val kind = "HelloWorldServletCount"
    val count = Option(ds.prepare(new Query(kind)).asSingleEntity()) getOrElse {
      val entity = new Entity(kind)
      entity.setProperty("count", 0)
      ds.put(entity)
      entity
    }
    require(null != count.getKey)
    val result = count.getProperty("count").asInstanceOf[java.lang.Number].intValue() + 1
    count.setProperty("count", result)
    ds.put(count)
    result
  }

}
