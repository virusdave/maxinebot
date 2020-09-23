package virusdave.zio.ops

import virusdave.ops.{OptionOps => RawOptionOps}
import zio.{IO, UIO, ZIO}

trait OptionOps extends RawOptionOps {
  implicit class _ZioRawOptionOps[A](private val in: Option[A]) {
    def zio: UIO[Option[A]] = ZIO.succeed(in)
    def zioOrFailWith[E](err: E): IO[E, A] = in.fold2(ZIO.fail(err), ZIO.succeed(_))
  }
}