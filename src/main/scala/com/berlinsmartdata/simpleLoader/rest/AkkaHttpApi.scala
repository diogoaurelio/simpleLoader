package com.berlinsmartdata.simpleLoader.rest


import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._


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
    // the default route
    pathPrefix("") {
      get {
        complete(stdResponse(pseudoNetworkOperation()))
        }
      }
    }

  def pseudoNetworkOperation(latency:Int=3000):String = {
    logger.info(s"Emulating network latency (${latency} ms) in a HTTP request")
    Thread.sleep(latency)
    "Finally done!"
  }
}

object AkkaHttpApi extends AkkaApiService {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatchers.lookup("my-dispatcher")
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
    System.exit(1)
  }

}
