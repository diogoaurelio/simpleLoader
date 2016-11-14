package com.berlinsmartdata.simpleLoader.basicExamples


import java.util

import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import play.api.libs.ws.{DefaultWSClientConfig, WSResponse}
import scala.concurrent.{Await, Promise, Future}
import play.api.libs.json._
// provide an execution context
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Example using Play WS API
  */

object ScalaPlayWSHttpRequest extends App {

  def getWeatherForState(wUrl:String, ws: NingWSClient): Future[WSResponse] = {

    val result = ws.url(wUrl).get
    result
  }

  def getBody(f:WSResponse): Future[String] = Future {
    f.body
  }

  def myJob(wUrl:String, ws: NingWSClient) = {
    for {
      f <- getWeatherForState(wUrl, ws)
      r <- getBody(f)
    } yield r
  }

  def loadAsyncV1(wUrl:String, client: NingWSClient): Future[Seq[String]] = {

    val async1 : Future[String] = myJob(wUrl, client)
    val async2 : Future[String] = myJob(wUrl, client)
    val async3 : Future[String] = myJob(wUrl, client)
    for {
      a1 <- async1
      a2 <- async2
      a3 <- async3
    } yield Seq(a1, a2, a3)
  }

  def controller(wUrl:String) = {
    val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build
    val builder = new AsyncHttpClientConfig.Builder(config)
    val client = new NingWSClient(builder.build)
    val myFutureResults = new util.HashMap[Int, String](10)

    val results = Await.result(loadAsyncV1(wUrl:String, client: NingWSClient), 2 minutes)
    client.close()
    for(i <- 0 to results.length-1) {
      println(results(i))
    }
  }

  def controller2(wUrl:String, numRequests:Int) = {
    val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig()).build
    val builder = new AsyncHttpClientConfig.Builder(config)
    val client = new NingWSClient(builder.build)

    val myFutureResults = new util.HashMap[Int, String](numRequests)
    val asyncs: Seq[Future[String]] = (0 to numRequests).map(x => myJob(wUrl, client))
    Future.sequence(asyncs).map{
      case results =>
        var count = 0
        for(i <- 1 to results.length-1) {
          myFutureResults.put(i, results(i))
          println(results(i))
          count+=1
        }
        println(s"Count: $count")
        client.close()
    }.recover {
      case error =>
        println(s"Exception occurred: ${error}")
        client.close()
    }
  }

  val cityCode = "CA"
  val state = "San_Francisco"
  val wUrl = s"http://api.wunderground.com/api/5a7c66db0ba0323a/conditions/q/${cityCode}/${state}.json"

  controller2(wUrl:String, 3)


}
