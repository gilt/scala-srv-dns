package com.gilt.util.net.dns

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

@RunWith(classOf[JUnitRunner])
class ServiceNameSpec
  extends Specification {

  "ServiceName" should {
    "accept valid service names" in {
      Seq(
        "foo",
        "foo-bar",
        "foo.bar",
        "foo-bar.baz",
        "ha.ha.ha",
        "service.some.public.site.com."
      ).map { n =>
        (ServiceName(n).name) mustEqual(n) // just make sure it doesn't explode
      }
    }

    "reject invalid service names" in {
      Seq(
        "foo-",
        "-foo",
        "!@#",
        "_foo"
      ) map { n =>
        (ServiceName(n).name) must throwA[InvalidServiceNameException]
      }
    }

    "split parts" in {
      ServiceName("foo").parts mustEqual Seq("foo")
      ServiceName("foo.bar").parts mustEqual Seq("foo", "bar")
    }

    "accept relative/absolute" in {
      ServiceName("foo.bar.baz").isAbsolute mustEqual(false)
      ServiceName("foo.bar.baz.").isAbsolute mustEqual(true)
    }
  }
}
