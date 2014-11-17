scala-srv-dns
=============

Scala convenience wrapper around org.xbill.DNS for SRV record lookups.

Usage
=============

Add a dependency to build.sbt:

    libraryDependencies ++= Seq(
      ...
      "com.gilt" %% "scala-srv-dns" % "0.0.5"
    )


Lookup records:

E.g. public record: 'sip' service at 'sip.voice.google.com'

    # sbt
    > console
    scala> import com.gilt.util.net.dns._
    scala> import com.gilt.util.net._

    scala> ServiceLookup.lookup("sip.sip.voice.google.com.", TransportProtocol.UDP)
    res0: Seq[com.gilt.util.net.dns.ServiceRecord] = ArrayBuffer(ServiceRecord(sip-anycast-1.voice.google.com./216.239.32.1,5060,10,1), ServiceRecord(sip-anycast-2.voice.google.com./216.239.32.2,5060,20,1))

With

    search something.gilt.com google.com

in

    /etc/resolv.conf

same example becomes

    scala> ServiceLookup.lookup("sip.sip.voice", TransportProtocol.UDP)
    res0: Seq[com.gilt.util.net.dns.ServiceRecord] = ArrayBuffer(ServiceRecord(sip-anycast-1.voice.google.com./216.239.32.1,5060,10,1), ServiceRecord(sip-anycast-2.voice.google.com./216.239.32.2,5060,20,1))


Development
=============
[OSSHR Guide](http://central.sonatype.org/pages/ossrh-guide.html)

[Using Sonatype](http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html)

[SBT/PGP](http://www.scala-sbt.org/sbt-pgp/usage.html)

