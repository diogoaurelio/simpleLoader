package com.berlinsmartdata.simpleLoader.utils


import com.ning.http.client.{AsyncHttpClientConfigBean, AsyncHttpClientConfig}
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import play.api.libs.ws.ssl.{SSLLooseConfig, SSLConfig}
import play.api.libs.ws.{WSClientConfig, DefaultWSClientConfig, WSResponse}
import scala.concurrent.Future
import play.api.libs.json._
// provide an execution context
import scala.concurrent.ExecutionContext.Implicits.global

object wsRequest {

    def request(wUrl:String, ws: NingWSClient,
                           headers:Option[Map[String,String]]=None,
                           queryStrings:Option[Map[String,String]]=None,
                           followRedirects:Boolean=false,
                           requestTimeOut:Option[Int]=None,
                           postData:Option[JsValue]=None
                          ): Future[WSResponse] = {
      var result = ws.url(wUrl).withFollowRedirects(followRedirects)
      headers match {
        case None =>
        case Some(map) => for((key,value) <- map) result = result.withHeaders(key-> value)
      }
      queryStrings match {
        case None =>
        case Some(map) => for((key, value) <- map) result = result.withQueryString(key -> value)
      }
      requestTimeOut match {
        case None =>
        case Some(x:Int) => result = result.withRequestTimeout(x)
      }
      postData match {
        case None => result.get
        case Some(jObj:JsValue) => result.post(jObj)
      }

    }

    def getBody(f:WSResponse): Future[String] = Future {
      f.body
    }

    def myJob(wUrl:String, ws: NingWSClient,
              headers:Option[Map[String,String]]=None,
              queryStrings:Option[Map[String,String]]=None,
              followRedirects:Boolean=false,
              requestTimeOut:Option[Int]=None,
              postData:Option[JsValue]=None
             ) = {
      for {
        f <- request(wUrl, ws, headers, queryStrings,
          followRedirects, requestTimeOut,postData)
        r <- getBody(f)
      } yield r
    }


    def controller(wUrl:String, numRequests:Int=3,
                   headers:Option[Map[String,String]]=None,
                   queryStrings:Option[Map[String,String]]=None,
                   followRedirects:Boolean=false,
                   requestTimeOut:Option[Int]=None,
                   postData:Option[JsValue]=None,
                   verbose:Boolean=true) = {

      val config = new NingAsyncHttpClientConfigBuilder(DefaultWSClientConfig(acceptAnyCertificate=Option(true))).build
      val builder = new AsyncHttpClientConfig.Builder(config)
      val client = new NingWSClient(builder.build)

      val asyncs: Seq[Future[String]] = (0 to numRequests).map(
        x => myJob(wUrl, client, headers, queryStrings,
          followRedirects, requestTimeOut,postData))
      Future.sequence(asyncs).map{
        case results =>
          for(i <- 1 to results.length-1) {
            if(verbose) println(s"\t- Request #${i} result: ${results(i)}")
          }
          client.close()
      }.recover {
        case error =>
          println(s"Exception occurred: ${error}")
          client.close()
      }
    }


}
