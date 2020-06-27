package experimental

import org.scalatest.flatspec._
import org.scalatest.matchers.should.Matchers

class HelloWorldTest extends AnyFlatSpec with Matchers {
  "HelloWorld" should "compile correctly" in {
    true
  }

  it should "output a string correctly" in {
    var out = ""
    new HelloWorld(str => out = str).main()
    out shouldBe "Hello, world!"
  }
}