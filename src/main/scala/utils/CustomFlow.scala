package utils

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.util.ByteString
import scodec.Attempt.{Failure, Successful}
import scodec.DecodeResult
import scodec.bits.ByteVector

import CommandsHandler.{_}

class CustomFlow() extends GraphStage[FlowShape[ByteString, ByteVector]] {

  val in = Inlet[ByteString]("ByteString.in")
  val out = Outlet[ByteVector]("ByteVector.out")

  override val shape: FlowShape[ByteString, ByteVector] = FlowShape(in, out)
  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler with OutHandler {

    var buffer = ByteVector.empty

    private def parse(): Unit = {

      buffer.slice(0, 4) match {
        case Commands.ReqPQValue => {

          Codecs.ReqPQCodec.decode(buffer.bits) match {
            case Successful(decodeResult: DecodeResult[(ByteVector, ByteVector)]) =>

              buffer = decodeResult.remainder.toByteVector
              val itemToEmit = generateRegPQ(decodeResult)
              push(out, itemToEmit)

            case Failure(cause) =>
              println(cause.messageWithContext)
          }
        }
        case Commands.ReqDHParamsValue => {
          Codecs.ReqDHParams.decode(buffer.bits) match {
            case Successful(decodeResult: DecodeResult[((((((ByteVector, ByteVector), ByteVector), Int), Int), Long), String)]) =>

              buffer = decodeResult.remainder.toByteVector
              val itemToEmit = generateReqDHParams(decodeResult)
              push(out, itemToEmit)
              completeStage()

            case Failure(cause) =>
              println(cause.messageWithContext)
          }
        }
        case buf if buf.size < 4 =>
          tryPull()
        case _ => {
          println("Unable to recognize received command")
        }
      }
    }

    private def tryPull(): Unit = {
      if (isClosed(in)) {
        completeStage()
      } else pull(in)
    }

    override def onPush(): Unit = {
      buffer ++= ByteVector(grab(in).toVector)
      parse()
    }

    override def onPull(): Unit = {
      pull(in)
    }

    setHandlers(in, out, this)

  }

}