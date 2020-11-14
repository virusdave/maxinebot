package virusdave.ops

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.language.higherKinds

trait IterableOps {
  implicit class _IterableOps[Z, Collection[+Element] <: Iterable[Element]](private val in: Collection[Z]) {

    // This one at least keeps the mutability hidden
    def partitionWith[L, R](fn: Z => Either[L, R])(
        implicit cbf1: CanBuildFrom[Nothing, L, Collection[L]],
        cbf2: CanBuildFrom[Nothing, R, Collection[R]]): (Collection[L], Collection[R]) = {
      val lhs = new mutable.ArrayBuffer[L](in.size)
      val rhs = new mutable.ArrayBuffer[R](in.size)
      in foreach fn.andThen {
        case Left(v) => lhs += v
        case Right(v) => rhs += v
      }
      (lhs.to[Collection], rhs.to[Collection])
    }

    // This one is more 'pure', but almost certainly less efficient.
    def _partitionWith2[L, R](fn: Z => Either[L, R])(
        implicit cbf1: CanBuildFrom[Nothing, L, Collection[L]],
        cbf2: CanBuildFrom[Nothing, R, Collection[R]]): (Collection[L], Collection[R]) = {
      val (lhs, rhs) = (in: Iterable[Z]).map(fn).map {
        case Left(v) => (Seq(v), Seq.empty[R])
        case Right(v) => (Seq.empty[L], Seq(v))
      }.unzip
      (lhs.flatten.to[Collection], rhs.flatten.to[Collection])
    }

    // Better naming
    def keepIf(pred: Z => Boolean)(implicit cbf1: CanBuildFrom[Nothing, Z, Collection[Z]]): Collection[Z] =
      in.filter(pred).to[Collection]
    def dropIf(pred: Z => Boolean)(implicit cbf1: CanBuildFrom[Nothing, Z, Collection[Z]]): Collection[Z] =
      in.filterNot(pred).to[Collection]
  }
}
