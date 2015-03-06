package com.tsmms.hackathon.helloservlet

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.xml.NodeSeq

/**
 * An example how a framework-less framework :-) might look like - when we are heavily using Scalas xml mode
 * to generate the html pages.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 24.02.2015
 */
class HelloDispatcher extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val controller = request.getServletPath + request.getPathInfo match {
      case HelloController.pathRegex(id) => new HelloController(id)(request)
    }
    controller.view() match {
      case Left(url: String) => response.sendRedirect(url)
      case Right(view: WebView) =>
        request.setAttribute("title", view.title)
        request.setAttribute("description", view.description)
        request.setAttribute("mainarea", view.mainarea)
        response.setDateHeader("Expires", 0)
        getServletContext.getRequestDispatcher("/test/frame.jsp").forward(request, response)
    }
  }

}

trait WebController {
  def view(): Either[String, WebView]
}

trait WebView {
  def title: String

  def description: String

  def mainarea: NodeSeq
}

trait HTMLRendering {
  def form(action: String, method: String = "POST")(nodeSeq: NodeSeq): NodeSeq =
    <form action={action} method={method}>
      {nodeSeq}
    </form>

  def submit(text: String) = <input type="submit" value={text}></input>

  def table(nodeSeq: NodeSeq) = <table border="1">
    {nodeSeq}
  </table>

  def tableRow(data: String*) = <tr>
    {data.map(element => <td>
      {element}
    </td>)}
  </tr>
}

object HelloController {
  val pathRegex = "/test/hellodispatch/huhu/([0-9]+)".r
}

// http://localhost:9090/hellodispatch/huhu/42?helloinput=17
class HelloController(id: String)(implicit val request: HttpServletRequest) extends WebController with WebView with HTMLRendering {
  override def view(): Either[String, WebView] = Right(this)

  val helloForm = new HelloForm()
  helloForm.field.value = helloForm.field.value + "x"

  val somedata = Map(1 -> 3, 5 -> 7)

  def title = "Hello world view"

  def description = "Hello world via the controller!"

  def mainarea = <p>Hoi! Ha!</p> ++ form(request.getServletPath + request.getPathInfo, "GET")(helloForm.render ++ submit("Redisplay")) ++
    table(somedata.toList.map(e => tableRow(e._1.toString, e._2.toString)))
}

class HelloForm(implicit val request: HttpServletRequest) {
  val field = new TextField("helloinput")

  def render: NodeSeq = field.render
}

class TextField(val name: String)(implicit val request: HttpServletRequest) {
  var value: String = request.getParameter(name)

  def render: NodeSeq = <input type="text" name={name} value={value}></input>
}
