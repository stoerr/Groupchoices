package com.tsmms.hackathon.hellowicket

import com.tsmms.hackathon.hellowicket.page.HelloPage
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.protocol.http.WebApplication

/**
 * The entry of the wicket application.
 */
class HelloWicketApplication extends WebApplication {
  override def getHomePage: Class[_ <: WebPage] = classOf[HelloPage]
}
