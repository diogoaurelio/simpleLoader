package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{HttpRequester, JobConfiguration, Utils}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.StdIn

/*
 * Shows how NOT to use for-comprehension
 * in case non-blocking behaviour is intended
 *
 */


object ConcurrentHttpScalaj02 extends App with HttpRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")

  /* Note: this is executed in sequence, NOT in parallel */
  Utils.measureDuration {
    val result = for {
      a <- Future(getRequest(address))
      b <- Future(getRequest(address))
      c <- Future(getRequest(address))
    } yield (a, b, c)
    Await.result(result, 10 seconds)
  }

  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
