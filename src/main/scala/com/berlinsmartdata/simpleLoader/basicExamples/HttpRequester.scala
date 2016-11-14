package com.berlinsmartdata.simpleLoader.basicExamples

import scalaj.http._

/**
  * Trait for generic scalaj HTTP requests
  *
  */
trait HttpRequester {

  def getRequest(address:String, timeOut:Int = 10000): HttpResponse[String] = {
    val wUrl = s"http://${address}/"
    println(s"Hitting URL ${wUrl}")
    Http(wUrl).timeout(connTimeoutMs = timeOut, readTimeoutMs = timeOut).asString
  }

  def printResponse(response: HttpResponse[String]) = {
    println(s"Status code: ${response.code}")
    println(response.body)
  }

  def response(implicit address:String) = {
    val response = getRequest(address)
    printResponse(response)
  }

}
