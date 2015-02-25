package com.tsmms.hackathon.choices.hello

import java.util.logging.Logger
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import com.google.appengine.api.datastore.{DatastoreServiceFactory, Entity, Query}

import scala.xml.NodeSeq

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
    req.setAttribute("title", "Hello world!")
    req.setAttribute("description", "This is a hello world page!")
    val body: NodeSeq = <p>We do have
      {callCount}
      calls</p> ++
      <p>
        <a href="/hellodispatch/huhu/42?helloinput=17">Hello world dispatcher</a>
      </p>
    req.setAttribute("mainarea", body)
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
