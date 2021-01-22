package virusdave.zio.ops

import zio.ZIO
import zio.logging.{Logging, log}

trait ZioOps {
  implicit class _ZioOps[R, E, A](private val in: ZIO[R, E, A]) {
    def logError: ZIO[R with Logging, E, A] = in.tapError(e => log.error(s"ZIO FAILED: $e"))
  }
}