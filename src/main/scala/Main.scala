import akka.NotUsed

import scala.concurrent.Future
import akka.stream._
import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Source, Tcp}
import akka.util.ByteString
import scodec.bits._
import utils.{CustomFlow, MTProtoRequest, MTProtoResponse}

object Main extends App {

  implicit val as = ActorSystem()
  implicit val am = ActorMaterializer()

  val host = "127.0.0.1"
  val port = 8888

  val connections: Source[IncomingConnection, Future[ServerBinding]] =
    Tcp().bind(host, port)


  connections runForeach { connection ⇒
    println(s"New connection from: ${connection.remoteAddress}")

    val echo = Flow[ByteString]
      .via(Flow.fromGraph[ByteString, MTProtoRequest, NotUsed](new CustomFlow()))
      .map(_.generateResponse)
      .map(_.toByteString)

    connection.handleWith(echo)
  }


}