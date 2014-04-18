package com.gilt.util.net.dns

import com.gilt.util.net.TransportProtocol

/** Helper class to construct ServiceLookup query.
  *
  * http://en.wikipedia.org/wiki/SRV_record
  * http://tools.ietf.org/html/rfc2782
  *
  *  SRV record format:
  *     _service._proto.name. TTL class SRV priority weight port target.
  *
  * @see ServiceLookup
  */
private[dns]
class SrvQuery (
  serviceName: ServiceName,
  transportProtocol: TransportProtocol,
  defaultDomain: String
) {
  require(serviceName != null, "serviceName must not be null")
  require(transportProtocol != null, "transportProtocol must not be null")
  require(defaultDomain != null, "defaultDomain must not be null")

  /** Construct _service._proto.name. part. */
  def query: String = {
    val serviceNameParts = serviceName.parts
    assert(!serviceNameParts.isEmpty, "Service name validation failed, empty service name parts")

    val srvService = "_" + serviceNameParts.head.toLowerCase
    val srvProto = "_" + transportProtocol.toString.toLowerCase

    // We assume all service names are partial and append default search domain,
    // this allows for some operational flexibility, we can have appropriate default search domains
    // for IAD/PHX/pods/dev and service clients can effectively hard-code service name part
    val srvName = (serviceNameParts.tail ++ defaultDomainParts).mkString(".")

    Seq(srvService, srvProto, srvName, "").mkString(".")
  }

  private[this]
  def defaultDomainParts: Seq[String] = defaultDomain.split('.')
}
