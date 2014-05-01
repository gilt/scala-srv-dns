package com.gilt.util.net.dns

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import com.gilt.util.net.TransportProtocol

@RunWith(classOf[JUnitRunner])
class SrvQuerySpec
  extends Specification {

  "SrvQuery" should {
    "generate query for simple serice names" in {
      (new SrvQuery(ServiceName("foo"), TransportProtocol.TCP, Some("gilt.com"))).query mustEqual "_foo._tcp.gilt.com."
      (new SrvQuery(ServiceName("foo"), TransportProtocol.TCP, Some("gilt.com."))).query mustEqual "_foo._tcp.gilt.com."
    }

    "generate query for serice names with domain part" in {
      (new SrvQuery(ServiceName("mongo.svc-foo"), TransportProtocol.TCP, Some("gilt.com"))).query mustEqual "_mongo._tcp.svc-foo.gilt.com."
      (new SrvQuery(ServiceName("mongo.svc-foo.phx"), TransportProtocol.TCP, Some("gilt.com"))).query mustEqual "_mongo._tcp.svc-foo.phx.gilt.com."
    }

    "generate query for without search path" in {
      (new SrvQuery(ServiceName("service.foo.bar.com."), TransportProtocol.TCP, None)).query mustEqual "_service._tcp.foo.bar.com."
    }
  }
}
