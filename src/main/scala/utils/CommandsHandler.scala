package utils

import scodec.{DecodeResult}
import scodec.bits.{ByteVector}

object CommandsHandler {

  def generateRegPQ(decodeResult: DecodeResult[(ByteVector, ByteVector)]) : ReqPQ = ReqPQ(decodeResult.value._2)
  def generateReqDHParams(decodeResult: DecodeResult[((((((ByteVector, ByteVector), ByteVector), Int), Int), Long), String)]) : ReqDHParams = {

    val encrypted_data = decodeResult.value._2
    val public_key_fingerprint = decodeResult.value._1._2
    val q = decodeResult.value._1._1._2
    val p = decodeResult.value._1._1._1._2
    val server_nonce = decodeResult.value._1._1._1._1._2
    val nonce = decodeResult.value._1._1._1._1._1._2

    ReqDHParams(nonce, server_nonce, p, q, public_key_fingerprint, encrypted_data)

  }

}
