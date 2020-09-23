package virusdave.ops

trait OptionOps {
  implicit class _OptionOps[A](private val in: Option[A]) {
    def fold2[B](ifEmpty: => B, ifDefined: A => B): B = in.fold(ifEmpty)(ifDefined)
  }
}
