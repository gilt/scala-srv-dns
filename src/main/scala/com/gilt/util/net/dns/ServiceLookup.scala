package com.gilt.util.net.dns

import org.xbill.DNS
import com.gilt.util.net.TransportProtocol
import scala.annotation.tailrec
import java.net.InetAddress
import org.slf4j.LoggerFactory

/** A thin wrapper around http://www.dnsjava.org/
  *  to make use of DNS SRV records for dynamic
  *  server/cluster configuration.
  *
  *  http://en.wikipedia.org/wiki/SRV_record
  *  http://tools.ietf.org/html/rfc2782
  *
  *  SRV record format:
  *     _service._proto.name. TTL class SRV priority weight port target.
  *
  *   service: the symbolic name of the desired service.
  *   proto: the transport protocol of the desired service; this is usually either TCP or UDP.
  *   name: the domain name for which this record is valid, ending in a dot.
  *   TTL: standard DNS time to live field.
  *   class: standard DNS class field (this is always IN).
  *   priority: the priority of the target host, lower value means more preferred.
  *   weight: A relative weight for records with the same priority.
  *   port: the TCP or UDP port on which the service is to be found.
  *   target: the canonical hostname of the machine providing the service, ending in a dot.
  *
  *   SRVRecord(Name name, int dclass, long ttl, int priority, int weight, int port, Name target)
  */
trait ServiceLookup { // for dependency injection, unit tests

  /** Lookup service record(s).
    *
    * E.g. given this /etc/resolv.conf
    *   search foo.bar.gilt.internal
    *
    * lookup("rabbitmq") will query for "_rabbitmq._tcp.foo.bar.gilt.internal."
    * lookup("rabbitmq.some.app") will query for "_rabbitmq._tcp.some.app.foo.bar.gilt.internal."
    *
    * Default search paths will be ignored if name ends with '.'
    *
    * lookup("service.some.public.site.com.") will query for "_service._tcp.some.public.site.com."
    *
    * @param serviceName 'canonical' name of a service or a service cluster
    * @param transportProtocol TCP/UDP
    * @throws InvalidServiceNameException
    * @throws ServiceLookupException
    */
  def lookup(serviceName: String,
             transportProtocol: TransportProtocol = TransportProtocol.TCP): Seq[ServiceRecord]
}

object ServiceLookup
  extends ServiceLookup {

  private[this]
  val log = LoggerFactory.getLogger(getClass.getName)

  private[this]
  val DnsSearchPaths: List[String] = getDnsSearchPaths()

  log.info("started with DNS search paths " + DnsSearchPaths)

  override def lookup(nameStr: String,
                      transportProtocol: TransportProtocol): Seq[ServiceRecord] = {

    val serviceName = ServiceName(nameStr) // outside of try/catch, has own validation exception to throw

    try {
      if (serviceName.isAbsolute) {
        runQuery(new SrvQuery(serviceName, transportProtocol, None).query)

      } else if (DnsSearchPaths.isEmpty) { // check every time and not just on startup to get message closer to the end of the log file
        if (log.isWarnEnabled) log.warn("Expected to find default DNS search path(s) in resolv.conf but none are defined")
        Seq.empty // don't explode, this may be a dev node and caller may want to default to something like localhost

      } else {
        lookup(serviceName, transportProtocol, DnsSearchPaths)
      }
    } catch {
      case e: Exception => throw new ServiceLookupException(nameStr, e)
    }
  }

  @tailrec
  private[this]
  def lookup(serviceName: ServiceName,
             transportProtocol: TransportProtocol,
             dnsSearchPaths: List[String]): Seq[ServiceRecord] = {

    dnsSearchPaths match {
      case sp :: sps => val records = runQuery(new SrvQuery(serviceName, transportProtocol, Some(sp)).query)
                        if (records.isEmpty) {
                          lookup(serviceName, transportProtocol, sps)
                        } else {
                          records
                        }
      case Nil => Seq.empty
    }
  }

  private[this]
  def runQuery(what: String): Seq[ServiceRecord] = {
    if (log.isDebugEnabled) log.debug("looking up " + what)

    val l = new DNS.Lookup(what, DNS.Type.SRV, DNS.DClass.IN)
    l.run()

    if (l.getResult() == DNS.Lookup.SUCCESSFUL) {
      Option(l.getAnswers().toSeq).getOrElse(Seq.empty).map(toServiceRecord(_))
    } else {
      Seq.empty
    }
  }

  private[this]
  def toServiceRecord(r: DNS.Record): ServiceRecord = {
    // This should better be an SVR record because we've asked for one, should be a 'hard' error otherwise.
    // E.g. returning Option[ServiceRecord] here will mask an error.
    val srvR = r.asInstanceOf[DNS.SRVRecord]

    ServiceRecord (
      InetAddress.getByName(srvR.getTarget.toString),
      srvR.getPort,
      srvR.getPriority,
      srvR.getWeight
    )
  }

  private[this]
  def getDnsSearchPaths(): List[String] = {
    // N.B.
    // The 'search' and 'domain' statements in /etc/resolv.conf are mutually exclusive and may not appear more than once.
    // Since 'search' is more powerful, 'domain' should not be used.
    // Xbill package simply filters out 'domain' statement, only cares about 'search'.
    val systemSearchPaths: List[DNS.Name] = Option(DNS.Lookup.getDefaultSearchPath).map(_.toList).getOrElse(Nil)
    systemSearchPaths.map(_.toString)
  }
}

case class ServiceLookupException(
  serviceName: String,
  cause: Exception
) extends Exception(
  "Failed to lookup [" + serviceName + "]: " + cause.getMessage,
  cause
)
