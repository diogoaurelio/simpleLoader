package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.JobConfiguration
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.io.StdIn
import scalaj.http._

/*
 * Shows a blocking example of a HTTP request
 */

object blockingHttpScalaj extends App {


  def getRequest(host:String, port:Int): HttpResponse[String] = {
    val wUrl = s"http://${host}:${port}/"
    println(s"Querying ${wUrl}")
    Http(wUrl).asString
  }
  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")

  def response = {
    val response = getRequest(host, port)
    println(response.body)
    println(response.code)
    println(response.headers)
    println(response.cookies)
  }
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Server online at http://${host}:${port}/")
  response
  println(s"Press RETURN to stop...")
  StdIn.readLine() // let it run until user presses return

}
