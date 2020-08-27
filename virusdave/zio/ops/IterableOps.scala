package virusdave.zio.ops

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import virusdave.zio.iterable.Shuffle
import virusdave.zio.iterable.Shuffle.{Choice, ChosenValue, UpperBound}
import zio.random.Random
import zio.{IO, URIO}

trait IterableOps {
  implicit class _ZioIterableOps[Z, Collection[+Element] <: Iterable[Element]](private val in: Collection[Z]) {
    def shuffleCapturingPermutation()(
        implicit cbf: CanBuildFrom[Collection[Z], Z, Collection[Z]])
    : URIO[Random, (Collection[Z], List[(UpperBound, ChosenValue)])] =
      Shuffle.capturing(in)

    def shuffleReplayingPermutation(permutation: List[Choice])(
        implicit cbf: CanBuildFrom[Collection[Z], Z, Collection[Z]])
    : IO[Shuffle.ShuffleError, (Collection[Z], List[Choice])] =
      Shuffle.replaying(in, permutation)
  }
}