package experimental

class HelloWorld(print: String => Unit) {
  def main(): Unit = print("Hello, world!")
}
