package com.berlinsmartdata.simpleLoader.basicExamples

import java.util.concurrent.Executors

import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{HttpRequester, JobConfiguration, Utils}
import scala.concurrent.duration._
import scala.concurrent.{Future, Await}
import scala.io.StdIn

/*
 * Shows how to generate X amount of
 * parallel non-blocking requests
 *
 */


object ConcurrentHttpScalaj04 extends App with HttpRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at http://${address}/")

  val numRequests = 100
  // Uncomment next line to use default thread-pool environment
  //implicit val ec =  scala.concurrent.ExecutionContext.Implicits.global

  // OR use custom => which you can find at ./src/main/resources/application.conf
  implicit val ec = new scala.concurrent.ExecutionContext {
    val threadPool = Executors.newFixedThreadPool(conf.getInt("thread-pool.execution-context"))
    override def reportFailure(cause: Throwable): Unit = {}
    override def execute(runnable: Runnable): Unit = threadPool.submit(runnable)
    def shutdown() = threadPool.shutdown()
  }

  Utils.measureDuration {
    val blockOfCode = dynamicRequests(address, numRequests=numRequests)
    Await.result(blockOfCode, 60 seconds)
  }
  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
