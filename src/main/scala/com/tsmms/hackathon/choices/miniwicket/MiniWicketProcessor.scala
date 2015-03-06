package com.tsmms.hackathon.choices.miniwicket

import javax.servlet.http.HttpServletRequest

import scala.xml._

/**
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 03.03.2015
 */
object MiniWicketProcessor {

  val wicketNS = "http://wicket.apache.org"

  /** contains a Map[String, NodeSeq] wicket:id to content */
  private val requestAttributeWicketAttributes = "requestAttributeWicketIdToValue"

  private type Processor = (Node) => NodeSeq

  private def processorMap(implicit request: HttpServletRequest): Map[String, Processor] =
    Option(request.getAttribute(requestAttributeWicketAttributes).asInstanceOf[Map[String, Processor]]).getOrElse(
      processorMap = Map())

  private def processorMap_=(map: Map[String, Processor])(implicit request: HttpServletRequest) = {
    request.setAttribute(requestAttributeWicketAttributes, map)
    map
  }

  def addField(id: String, value: NodeSeq)(implicit request: HttpServletRequest): Unit = {
    val fieldProcessor: Processor = (node: Node) => node match {
      case elem@Elem(prefix, label, attribs, scope, children@_*) =>
        Elem(prefix, label, attribs, scope, false, value: _*)
    }
    processorMap = processorMap + (id -> fieldProcessor)
  }

  def addField(id: String, value: String)(implicit request: HttpServletRequest): Unit = addField(id, new Text(value))

  def addRepeater(id: String, elements: Seq[() => Unit])(implicit request: HttpServletRequest): Unit = {
    val repeaterProcessor: Processor = node => elements flatMap (element => {
      val oldProcessorMap = processorMap
      element() // changes processorMap temporarily
      val Elem(prefix, label, attribs, scope, children@_*) = node
      val processedChildren = children.flatMap(wicketyTransformer(_))
      val result = Elem(prefix, label, attribs, scope, false, processedChildren: _*)
      processorMap = oldProcessorMap
      result
    })
    processorMap = processorMap + (id -> repeaterProcessor)
  }

  def addAction(id: String, url: String)(implicit request: HttpServletRequest): Unit = {
    val actionProcessor: Processor = (node: Node) => node match {
      case elem@Elem(prefix, label, attribs, scope, children@_*) =>
        Elem(prefix, label, attribs, scope, false, children: _*) % Attribute(null, "action", url, Null)
    }
    processorMap = processorMap + (id -> actionProcessor)
  }

  private def getWicketIdAction(id: String, request: HttpServletRequest): Processor = processorMap(request)(id)

  def wicketyTransformer(xml: NodeSeq)(implicit request: HttpServletRequest): NodeSeq =
    xml flatMap (node => node match {
      case elem: Elem if elem.attribute(wicketNS, "id").isDefined =>
        val id = node.attribute(wicketNS, "id").get(0).text
        val action: Processor = getWicketIdAction(id, request)
        action(elem)
      case elem@Elem(prefix, label, attribs, scope, children@_*) if elem.attribute(wicketNS, "remove").isDefined =>
        Text("")
      case Elem(prefix, label, attribs, scope, children@_*) =>
        Elem(prefix, label, attribs, scope, false, wicketyTransformer(children): _*)
      case other => other
    })

}
