package com.berlinsmartdata.simpleLoader.basicExamples

import scala.concurrent._
import ExecutionContext.Implicits.global
import scalaj.http._
import scala.util.{Success, Failure}

/*
 * Shows near almost non-blocking version of the previous example of a HTTP request in
 * com.berlinsmartdata.simpleLoader.basicExamples.scalaBlockingRequestExample
 * Why near non-blocking? Well for every client connection, you get a thread
 * blocked inside the implicit ExecutionContext; this potentially means you
 * can exaust the thread pool => so looks more like traditional threading
 */


object concurrentHttpScalaj {

  def getWeatherForState(cityCode:String, state:String): HttpResponse[String] = {
    // Check these instructions on how to query the wunderground api: https://github.com/InitialState/wunderground-sensehat/wiki/Part-1.-How-to-Use-the-Wunderground-API
    val wUrl = s"http://api.wunderground.com/api/5a7c66db0ba0323a/conditions/q/$cityCode/$state.json"
    Http(wUrl).asString
  }

  def printResponse(response: HttpResponse[String]): Unit = {
    println(response.body)
    println(response.code)
    println(response.headers)
    println(response.cookies)
  }

  val response: Future[HttpResponse[String]] = Future {
    getWeatherForState("CA", "San_Francisco")
  }
  response onComplete {
    case Success(response) => printResponse(response)
    case Failure(t) => println(s"Exception: ${t.getMessage()}")
  }



}
