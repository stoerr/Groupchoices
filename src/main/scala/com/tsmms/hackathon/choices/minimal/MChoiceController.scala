package com.tsmms.hackathon.choices.minimal

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.collection.JavaConversions._
import scala.util.Random
import scala.xml.NodeSeq

object MChoiceController {
  def makeStringId = "" + math.abs(Random.nextLong())
}

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 28.02.2015
 */
class MChoiceController extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val r = request
    val page: MController = Option(request.getPathInfo).getOrElse("") match {
      case "" => new NewPoll()
    }
    processController(page, request, response)
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val r = request
    val page: MController = Option(request.getPathInfo).getOrElse("") match {
      case "/a" => new CreatePoll()
    }
    processController(page, request, response)
  }

  def processController(page: MController, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    page.view() match {
      case Left(url) => response.sendRedirect(request.getServletPath + url)
      case Right(nodeseq) => response.getWriter.print(nodeseq)
    }
  }
}

abstract class MController(implicit val request: HttpServletRequest) {
  def view(): Either[String, NodeSeq]

  val requestParameters: Map[String, String] = request.getParameterMap.toMap.asInstanceOf[Map[String, Array[String]]]
    .mapValues(_(0))

  def param(name: String) = requestParameters(name)

  def htmlPage(title: String, main: NodeSeq) = <html>
    <head>
      <title>
        {title}
      </title>
    </head> <body>
      <h1>
        {title}
      </h1>{main}
    </body>
  </html>

  def form(action: String, content: NodeSeq) =
    <form action={request.getServletPath + action} method="POST">
      {content}
    </form>


  def textInput(name: String, description: String) = <div>
    <label for={name}>
      {description}
    </label> <input name={name} id={name}></input>
  </div>

  def submit(name: String, description: String) = <input type="submit" value={description} name={name}></input>
}

class NewPoll(implicit request: HttpServletRequest) extends MController {
  override def view() = Right(htmlPage("Create a new poll", form("/a",
    textInput("name", "Name") ++ textInput("description", "Description") ++ textInput("choice1", "Choice 1") ++
      textInput("choice2", "Choice 2") ++ textInput("choic3", "Choice 3") ++ textInput("choice4", "Choice 4") ++
      submit("submit", "Submit")
  )))
}

class CreatePoll(implicit request: HttpServletRequest) extends MController {
  val choices = requestParameters.filterKeys(_.startsWith("choice")).toArray.sortBy(_._1).map(p => MChoice
    (MChoiceController.makeStringId, p._2))
  val poll = MPoll(name = param("name"), description = param("description"), choices = choices.toList)
  println(poll)

  override def view() = Left("/a/42")
}
