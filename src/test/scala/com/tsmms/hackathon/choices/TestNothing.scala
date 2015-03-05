package com.tsmms.hackathon.choices

import org.scalatest.FlatSpec

/**
 * Just an empty test
 * @author <a href="http://www.stoerr.net/">Hans-Peter Stoerr</a>
 * @since 20.02.2015
 */
class TestNothing extends FlatSpec {

  "Nothing" should "be OK" in {
    assert(true)
  }

  "AbstractController.transpose" should "transpose" in {
    val l = List(List(1, 2), List(3, 4))
    val lt = AbstractController.transpose(l)
    assert(List(List(1, 3), List(2, 4)) == lt)
  }

}
