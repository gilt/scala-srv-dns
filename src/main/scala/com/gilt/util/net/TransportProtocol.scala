package com.gilt.util.net

/** Net transport protocol constants: TCP/UDP. */
trait TransportProtocol

object TransportProtocol {
  case object TCP extends TransportProtocol
  case object UDP extends TransportProtocol
}
