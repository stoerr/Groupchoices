package com.tsmms.hackathon.choices

import javax.servlet.http.HttpServletRequest

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Various stuff used often in controllers.
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 03.03.2015
 */
abstract class AbstractController(request: HttpServletRequest) {

  val requestParameters: Map[String, String] = request.getParameterMap.toMap.asInstanceOf[Map[String, Array[String]]]
    .mapValues(_(0))

  def parameter(name: String) = requestParameters(name)

  def choiceParameters = requestParameters.filterKeys(_.startsWith("choice")).filterNot(_._2.trim.isEmpty)
    .toArray.sortBy(_._1).map(p => p._2).toList

}

object AbstractController {

  def encodeId(id: Long) = java.lang.Long.toString(id, Character.MAX_RADIX)

  def decodeId(encodedId: String) = java.lang.Long.parseLong(encodedId, Character.MAX_RADIX)

  def makeRandomEncodedId(): String = encodeId(Random.nextLong())

  def transpose[T](matrix: List[List[T]]): List[List[T]] = matrix match {
    case head :: tail if !head.isEmpty => matrix.map(_.head) :: transpose(matrix.map(_.tail))
    case _ => List.empty
  }

}
