package com.berlinsmartdata.simpleLoader.utils

import scalaj.http._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Trait for generic scalaj HTTP requests
  *
  */
trait ScalaJHttpRequester {

  def getRequest(address:String,
                 timeOut:Int = 10000,
                 id:Option[String]=None,
                 headers:Option[Map[String, String]]=None): HttpResponse[String] = {
    val wUrl = s"http://${address}/"
    id match {
      case Some(s) => println(s"Hitting URL ${wUrl} (via future id: ${id})")
      case _ => println(s"Hitting URL ${wUrl}")
    }
    lazy val res = Http(wUrl)
      .timeout(connTimeoutMs = timeOut, readTimeoutMs = timeOut)
    headers match {
        case Some(map) => res.headers(map).asString
        case _ => res.asString
    }

  }

  def printResponse(response: HttpResponse[String]) = {
    println(s"Status code: ${response.code}")
    println(response.body)
  }

  def response(implicit address:String) = {
    val response = getRequest(address)
    printResponse(response)
    response
  }

  def dynamicRequests(address:String,
                      timeOut:Int = 1000000,
                      id:Option[String]=None,
                      headers:Option[Map[String, String]]=None,
                      numRequests:Int=1)(implicit ec: ExecutionContext):Future[Seq[HttpResponse[String]]] = {

    val asyncs: Seq[Future[HttpResponse[String]]] = (0 to numRequests).map(
      x => Future(getRequest(address, timeOut=timeOut, id=Some(s"Future_${x}"))))
    Future.sequence(asyncs)
  }

}
