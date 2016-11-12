package com.berlinsmartdata.simpleLoader.rest

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException

import akka.http.javadsl.model.ContentTypes
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType

import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._
import scala.io.StdIn


case class stdResponse(res: String)

trait jsonSerializer extends DefaultJsonProtocol {
  implicit val stdResponseFormat = jsonFormat1(stdResponse.apply)
}

trait AkkaApiService extends jsonSerializer {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def conf: Config
  val logger: LoggingAdapter

  val routes = {
    pathPrefix("") {
      get {
        complete(stdResponse(pseudoNetworkOperation))
        }
      }
    }

  def pseudoNetworkOperation:String = {
    logger.info("Emulating network latency in a HTTP request")
    Thread.sleep(5000)
    "Finally done!"
  }
}

object AkkaHttpApi extends AkkaApiService {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val conf = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  val host = conf.getString("akka-http.host")
  val port = conf.getInt("akka-http.port")
  implicit val timeout = Timeout(30 seconds)



  Http().bindAndHandle(routes, host, port) map { binding =>
    logger.info(s"Server online at ${binding.localAddress}")
  } recover { case ex =>
    logger.info(s"It seems that API daemon cannot bind to $host:$port", ex.getMessage)
  }

}
