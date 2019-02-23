package utils

import akka.util.ByteString
import scodec.bits.ByteVector
import utils.Codecs.{_}

sealed trait MTProtoRequest {
  def generateResponse: MTProtoResponse
}

sealed trait MTProtoResponse {
  def toByteString: ByteString
}

case class ReqPQ(nonce: ByteVector) extends MTProtoRequest {

  override def generateResponse: MTProtoResponse = {
    ResPQ(
      ByteVector.low(16),
      ByteVector.low(16),
      10,
      10,
      1L
    )
  }
}

case class ResPQ(nonce: ByteVector,
                 server_nonce: ByteVector,
                 p: Int,
                 q: Int,
                 server_public_key_fingerprints: Long) extends MTProtoResponse {
  override def toByteString: ByteString = {
    val encoded_byte_vector = ResPQCodec.encode(Commands.ResPQValue -> nonce -> server_nonce -> p -> q -> server_public_key_fingerprints).getOrElse(
      throw new IllegalStateException("Something goes wrong")
    ).toByteVector

    ByteString.fromArray(encoded_byte_vector.toArray)

  }
}

case class ReqDHParams(nonce: ByteVector,
                       server_nonce: ByteVector,
                       p: Int,
                       q: Int,
                       public_key_fingerprint: Long,
                       encrypted_data: String) extends MTProtoRequest {
  override def generateResponse: MTProtoResponse = {
    new MTProtoResponse {
      override def toByteString: ByteString = ByteString.empty
    }
  }
}
