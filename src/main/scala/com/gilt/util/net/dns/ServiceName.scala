package com.gilt.util.net.dns

/**
 * Name of the service in a format compatible with our use of DNS SRV record feature.
 * Basically a format-validated String.
 *
 * We support 2 use cases:
 *   - 'Simple' names, intended to represent globally shared services, like shared zookeeper cluster.
 *      E.g. 'zookeeper-cluster' or 'kafka-cluster'.
 *
 *   - Application-specific names, intended to represent services that are name spaced to a particular application domain.
 *     E.g. 'mongo.user' would represent default mongo DB service that is 'owned' by a 'user' app.
 *     Here, the part before the first '.' is the canonical name of the service, the rest is prepended to the default
 *     search domain and used as the domain part.
 *
 * @see ServiceLookup
 */
case class ServiceName (
  name: String
) {
  ServiceName.validateServiceName(name)

  /** '.' separated parts. */
  def parts: Seq[String] = name.split('.')

  /** Names that don't end with '.' we combine with search path. */
  def isAbsolute: Boolean = name.endsWith(".")
}

object ServiceName {

  private[this]
  val ValidServiceNameRe = {
    // e.g. 'foo'
    //       'foo-bar'
    //       'foo-bar.baz'
    val nameRe = """([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*)"""
    ("^" + nameRe + """(\.""" + nameRe + """)*\.?$""").r
  }

  /** Validates service name.
    * @throws InvalidServiceNameException when invalid
    */
  private
  def validateServiceName(name: String): Unit = name match {
    case null => throw new InvalidServiceNameException(name, "service name must not be null")
    case "" => throw new InvalidServiceNameException(name, "service name must not be empty")
    case ValidServiceNameRe(_,_,_,_,_) => () // all good
    case _ => throw new InvalidServiceNameException(name, "doesn't match regex pattern " + ValidServiceNameRe)
  }
}

class InvalidServiceNameException(
  serviceName: String,
  error: String
) extends Exception (
  "Invalid service name [" + serviceName + "]: " + error
)
