package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{JobConfiguration, Utils}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import scala.io.StdIn
import ExecutionContext.Implicits.global

/*
 * Shows near almost non-blocking version of the previous example of a HTTP request in
 * com.berlinsmartdata.simpleLoader.basicExamples.BlockingHttpScalaj
 * Why near non-blocking? Well for every client connection, you get a thread
 * blocked inside the implicit ExecutionContext; this potentially means you
 * can exhaust the thread pool => so looks more like traditional threading
 */


object ConcurrentHttpScalaj extends App with HttpRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")

  Utils.measureDuration{
    val r1 = Future(getRequest(address))
    val r2 = Future(getRequest(address))
    val r3 = Future(getRequest(address))
    val result = for {
      a <- r1
      b <- r2
      c <- r3
    } yield (a, b, c)
    Await.result(result, 10 seconds)
  }

  Utils.measureDuration {
    val r1 = Future(getRequest(address))
    val r2 = Future(getRequest(address))
    val r3 = Future(getRequest(address))
    val result = for {
      a <- r1
      b <- r2
      c <- r3
    } yield (a, b, c)
    Await.result(result, 10 seconds)
  }

  /* Note: this does NOT work => is executed in sequence, NOT in parallel
  Utils.measureDuration {
    val result = for {
      a <- Future(getRequest(address))
      b <- Future(getRequest(address))
      c <- Future(getRequest(address))
    } yield (a, b, c)
    Await.result(result, 10 seconds)
  }
  */
  
  Utils.measureDuration {
    val blockOfCode = {
      val a = Future(getRequest(address))
      val b = Future(getRequest(address))
      val c = Future(getRequest(address))
      Future.sequence(List(a,b,c))
    }
    Await.result(blockOfCode, 10 seconds)
  }

  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
