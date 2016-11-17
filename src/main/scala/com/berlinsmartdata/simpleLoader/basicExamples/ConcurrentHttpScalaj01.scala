package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{ScalaJHttpRequester, JobConfiguration, Utils}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import scala.io.StdIn
import ExecutionContext.Implicits.global

/*
 * Shows a near non-blocking version of the previous
 * example of a HTTP request with for-comprehension
 * Why near non-blocking? Well for every client connection, you get a thread
 * blocked inside the implicit ExecutionContext, since scalaj.http lib blocks threads;
 * this potentially means you can exaust the thread pool => so looks more like traditional threading
 *
 */


object ConcurrentHttpScalaj01 extends App with ScalaJHttpRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")

  Utils.measureDuration{
    val r11 = Future(getRequest(address, id=Some("Future11")))
    val r21 = Future(getRequest(address, id=Some("Future21")))
    val r31 = Future(getRequest(address, id=Some("Future31")))
    val result = for {
      a <- r11
      b <- r21
      c <- r31
    } yield (a, b, c)
    Await.result(result, 10 seconds)
  }


  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
