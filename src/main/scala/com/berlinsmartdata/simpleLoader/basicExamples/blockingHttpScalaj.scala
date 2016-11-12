package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{JobConfiguration, Utils}

import scala.io.StdIn
import scalaj.http._

/*
 * Shows a blocking example of a HTTP request
 */

object blockingHttpScalaj extends App {


  def getRequest(address:String, timeOut:Int = 10000): HttpResponse[String] = {
    val wUrl = s"http://${address}/"
    println(s"Hitting URL ${wUrl}")
    Http(wUrl).timeout(connTimeoutMs = timeOut, readTimeoutMs = timeOut).asString
  }

  def response(implicit address:String) = {
    val response = getRequest(address)
    println(s"Status code: ${response.code}")
    println(response.body)
    //println(response.headers)
  }

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")
  // 3x GET request
  Utils.measureDuration({response; response; response})

  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)

}
