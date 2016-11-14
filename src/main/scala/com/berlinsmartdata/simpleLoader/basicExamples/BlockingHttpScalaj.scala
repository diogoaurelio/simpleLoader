package com.berlinsmartdata.simpleLoader.basicExamples

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{JobConfiguration, Utils}

import scala.io.StdIn


/*
 * Shows an example of HTTP requests in a blocking fashion
 */

object BlockingHttpScalaj extends App with HttpRequester {

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
