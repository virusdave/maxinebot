package experimental

object HelloWorldMain {
  def main(args: Array[String]) = {
    new HelloWorld(println).main()
  }
}