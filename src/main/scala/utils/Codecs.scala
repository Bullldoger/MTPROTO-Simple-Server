package utils

import scodec._
import scodec.bits._
import scodec.codecs.{bytes, _}

object Codecs {

  val ReqPQCodec: Codec[(ByteVector, ByteVector)] =
    bytes(4) ~ bytes(16)

  val ResPQCodec: Codec[(((((ByteVector, ByteVector), ByteVector), Int), Int), Long)] =
    bytes(4) ~ bytes(16) ~ bytes(16) ~ int(4) ~ int(4) ~ long(64)

  val ReqDHParams: Codec[((((((ByteVector, ByteVector), ByteVector), Int), Int), Long), String)] =
    bytes(4) ~ bytes(16) ~ bytes(16) ~ int(4) ~ int(4) ~ long(64) ~ ascii32
}