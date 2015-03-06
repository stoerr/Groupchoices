package com.tsmms.hackathon.choices.miniwicket

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintWriter}
import java.util.logging.Logger
import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpServletResponseWrapper}

import scala.collection.mutable
import scala.xml._

/**
 * A servletfilter that simulates some behaviour of apache wicket. Just done since it is much easier use for now.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 02.03.2015
 */
class MiniWicketServletFilter extends Filter {

  val logger = Logger.getLogger(getClass.toString)

  var ignoredPaths: Array[String] = Array()

  override def init(filterConfig: FilterConfig): Unit = {
    ignoredPaths = filterConfig.getInitParameter("ignorePaths").split(",")
  }

  override def destroy(): Unit = {}

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    val httpRequest = request.asInstanceOf[HttpServletRequest]
    if (ignoredPaths.find(path => (httpRequest.getRequestURI).contains(path)).isDefined)
      chain.doFilter(request, response)
    else {
      val responseWrapper = new OutputCapturingServletResponseWrapper(response.asInstanceOf[HttpServletResponse])
      try {
        chain.doFilter(request, responseWrapper)
      } finally {
        responseWrapper.transformOutput(xhtmlTransformer(httpRequest))
      }
    }
  }

  def xhtmlTransformer(request: HttpServletRequest)(in: Array[Byte], out: PrintWriter): Unit = {
    if (in.length > 0) {
      val xml = XML.load(new ByteArrayInputStream(in))
      MiniWicketProcessor.wicketyTransformer(xml)(request) foreach (XML.write(out, _, "UTF-8", false, null))
    }
  }

}

class OutputCapturingServletResponseWrapper(response: HttpServletResponse) extends HttpServletResponseWrapper(response) {

  val capturedOutput = new ByteArrayOutputStream()

  override def getWriter: PrintWriter = new PrintWriter(getOutputStream)

  override def getOutputStream: ServletOutputStream = new ServletOutputStream {
    override def write(b: Int): Unit = capturedOutput.write(b)
  }

  def transformOutput(transformer: (Array[Byte], PrintWriter) => Unit): Unit = {
    val writer = super.getWriter
    transformer(capturedOutput.toByteArray, writer)
    writer.close()
    flushBuffer()
    capturedOutput.reset()
  }

}
