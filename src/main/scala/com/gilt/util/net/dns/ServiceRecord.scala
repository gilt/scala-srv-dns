package com.gilt.util.net.dns

import java.net.InetAddress

/** Used to create a canonical host:port connection string to some service or cluster.
  * @see ServiceLookup
  */
case class ServiceRecord(

  /** Name of the node providing this service (an internal name). */
  host: InetAddress,

  /** Service port. */
  port: Int,

  /** Priority of the target host. */
  priority: Int,

  /** Weight of this node. */
  weight: Int
) {
  require(host != null, "host must not be null")

  // http://tools.ietf.org/html/rfc2782
  checkUint16(port, "port")
  checkUint16(priority, "priority")
  checkUint16(weight, "weight")

  private[this]
  def checkUint16(x: Int, name: String): Unit = {
    require(x >= 0, name + " must be >= 0")
    require(x <= 65535, name + " must be <= 65535")
  }
}
