package virusdave.ops

trait AnyOps {
  implicit class _AnyOps[A](private val in: A) {
    def some: Option[A] = Option(in)
    def |>[B](fn: A => B): B = fn(in)
  }
}