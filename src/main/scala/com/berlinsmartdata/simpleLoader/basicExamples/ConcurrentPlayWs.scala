package com.berlinsmartdata.simpleLoader.basicExamples

import java.util.concurrent.Executors
import com.berlinsmartdata.simpleLoader.rest.AkkaHttpApi
import com.berlinsmartdata.simpleLoader.utils.{PlayWsRequester, Utils, JobConfiguration}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

/**
  * Created by diogo on 17.11.16.
  */
object ConcurrentPlayWs extends App with PlayWsRequester {

  val conf = JobConfiguration.getConfiguration()
  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val address = s"http://${host}:${port}"
  // start the server
  val api = AkkaHttpApi

  println(s"Starting App - Akka-HTTP API daemon live at ${address}/")

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

  val headersMap = Map(
    "Content-Type" -> "application/json"
    , "Charset" -> "UTF-8"
    //"x-api-key" -> x_api_key
  )

  Utils.measureDuration {
    val blockOfCode = controller(wUrl=address, numRequests=numRequests,
      headers=Some(headersMap), queryStrings=None, postData=None)
    Await.result(blockOfCode, 60 seconds)
  }
  println("Press RETURN to stop Akka-HTTP daemon...")
  StdIn.readLine() // let it run until user presses return
  println("Bye!")
  System.exit(0)
}
