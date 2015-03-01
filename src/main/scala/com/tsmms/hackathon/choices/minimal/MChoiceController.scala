package com.tsmms.hackathon.choices.minimal

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import scala.collection.JavaConversions._
import scala.util.Random
import scala.xml.NodeSeq

object MChoiceController {
  def makeStringId = "" + math.abs(Random.nextLong())

  def encodeId(id: Long) = java.lang.Long.toString(id, Character.MAX_RADIX)

  def decodeId(encodedId: String): Long = java.lang.Long.parseLong(encodedId, Character.MAX_RADIX)

  val pathNewPoll = ""
  val pathNewPollRegex = pathNewPoll.r

  val pathCreatePoll = "/c"
  val pathCreatePollRegex = pathCreatePoll.r

  def pathViewPoll(id: Long) = "/c/" + encodeId(id)

  val pathViewPollRegex = "/c/(-?[0-9a-z]+)".r

  def pathAnswerPoll(id: Long) = "/c/" + encodeId(id) + "/new"

  val pathAnswerPollRegex = "/c/(-?[0-9a-z]+)/new".r

  def pathSaveAnswerToPoll(id: Long) = "/c/" + encodeId(id) + "/new"

  val pathSaveAnswerToPollRegex = "/c/(-?[0-9a-z]+)/v".r

}

import com.tsmms.hackathon.choices.minimal.MChoiceController._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 28.02.2015
 */
class MChoiceController extends HttpServlet {

  override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val r = request
    val page: MController = Option(request.getPathInfo).getOrElse("") match {
      case pathNewPollRegex() => new NewPoll()
      case pathViewPollRegex(encodedId) => new ViewPoll(decodeId(encodedId))
      case pathAnswerPollRegex(encodedId) => new AnswerPoll(decodeId(encodedId))
    }
    processController(page, request, response)
  }

  override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    implicit val r = request
    val page: MController = Option(request.getPathInfo).getOrElse("") match {
      case pathCreatePollRegex() => new CreatePoll()
      case pathSaveAnswerToPollRegex(encodedId) => new SaveAnswerToPoll(decodeId(encodedId))
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

  def table(headings: Seq[String], content: Seq[Seq[String]]): NodeSeq = <table border="1">
    <tr>
      {headings map (h => <th>
      {h}
    </th>)}
    </tr>{content map (line => <tr>
      {line map (c => <td>
        {c}
      </td>
        )}
    </tr>
      )}
  </table>
}

class NewPoll(implicit request: HttpServletRequest) extends MController {
  override def view() = Right(htmlPage("Create a new poll", form(pathCreatePoll,
    textInput("name", "Name") ++ textInput("description", "Description") ++ textInput("choice1", "Choice 1") ++
      textInput("choice2", "Choice 2") ++ textInput("choic3", "Choice 3") ++ textInput("choice4", "Choice 4") ++
      submit("submit", "Submit")
  )))
}

class CreatePoll(implicit request: HttpServletRequest) extends MController {
  val choices = requestParameters.filterKeys(_.startsWith("choice")).toArray.sortBy(_._1).map(p => MChoice
    (MChoiceController.makeStringId, p._2))
  val poll = MPollDao.saveOrUpdate(MPoll(name = param("name"), description = param("description"), choices = choices
    .toList))
  println(poll)
  println(MPollDao.get(poll.id.get))

  override def view() = Left(pathViewPoll(poll.id.get))
}

class ViewPoll(id: Long)(implicit request: HttpServletRequest) extends MController {
  val poll = MPollDao.get(id).get

  override def view() = Right(htmlPage("Poll " + poll.name, <p>
    {poll.description}
  </p> ++ <a href={request.getServletPath + pathAnswerPoll(poll.id.get)}>Create new answer</a> ++ table(List
    ("Choice"), poll.choices.map(n =>
    List(n.name)))))
}

class AnswerPoll(id: Long)(implicit request: HttpServletRequest) extends MController {
  val poll = MPollDao.get(id).get

  override def view() = Right(htmlPage("Answer poll " + poll.name, <p>
    {poll.description}
  </p> ++ form(pathSaveAnswerToPoll(poll.id.get), poll.choices.zipWithIndex.map { case (choice, i) =>
    textInput(choice.id, choice.name)
  })))
}

class SaveAnswerToPoll(id: Long)(implicit request: HttpServletRequest) extends MController {
  val poll = MPollDao.get(id).get

  override def view() = Left(pathViewPoll(poll.id.get))
}
