package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{HttpRequester, JobConfiguration, Utils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.StdIn

/*
 * Shows an alternative non-blocking version of
 * the previous example of a HTTP request
 *
 */


object ConcurrentHttpScalaj03 extends App with HttpRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")

  Utils.measureDuration {
    val blockOfCode = {
      val a = Future(getRequest(address, id=Some("Future13")))
      val b = Future(getRequest(address, id=Some("Future23")))
      val c = Future(getRequest(address, id=Some("Future33")))
      Future.sequence(List(a,b,c))
    }
    Await.result(blockOfCode, 10 seconds)
  }

  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
