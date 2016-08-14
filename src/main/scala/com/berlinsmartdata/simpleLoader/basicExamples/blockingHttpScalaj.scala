package com.berlinsmartdata.simpleLoader.basicExamples

import scalaj.http._

/*
 * Shows a blocking example of a HTTP request
 */

object blockingHttpScalaj extends App {


  def getWeatherForState(cityCode:String, state:String): HttpResponse[String] = {
    // Check these instructions on how to query the wunderground api: https://github.com/InitialState/wunderground-sensehat/wiki/Part-1.-How-to-Use-the-Wunderground-API
    val wUrl = s"http://api.wunderground.com/api/5a7c66db0ba0323a/conditions/q/$cityCode/$state.json"
    Http(wUrl).asString
  }

  val response: HttpResponse[String] = getWeatherForState("CA", "San_Francisco")

  println(response.body)
  println(response.code)
  println(response.headers)
  println(response.cookies)


}
