package virusdave.zio.iterable

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable
import scala.language.higherKinds
import zio.random.Random
import zio.{IO, Ref, URIO, ZIO}

object Shuffle {
  type UpperBound = Int
  type ChosenValue = Int
  type Choice = (UpperBound, ChosenValue)

  sealed trait ShuffleError
  case class MissingPermutationDetails(calledWith: UpperBound) extends ShuffleError
  case class UnmatchedPermutationDetails(calledWith: UpperBound, expected: UpperBound) extends ShuffleError

  def capturing[Z, Collection[+Element] <: Iterable[Element]](
      in: Collection[Z])(
      implicit cbf: CanBuildFrom[Collection[Z], Z, Collection[Z]])
  : URIO[Random, (Collection[Z], List[Choice])] = {
    for {
      rng <- ZIO.access[Random](_.get)
      permutationRecord <- Ref.make(new mutable.ArrayBuffer[Choice])
      recordUpdatingNextIntBounded = (upper: Int) => {
        for {
          next <- rng.nextIntBounded(upper)
          _ <- permutationRecord.update(_ += ((upper, next)))
        } yield {
          next
        }
      }
      shuffled <- internalUsingNextIntBounded(in, recordUpdatingNextIntBounded)
      finalRecord <- permutationRecord.get
    } yield {
      (shuffled, finalRecord.to[List])
    }
  }

  def replaying[Z, Collection[+Element] <: Iterable[Element]](
      in: Collection[Z], permutation: List[Choice])(
      implicit cbf: CanBuildFrom[Collection[Z], Z, Collection[Z]])
  : IO[ShuffleError, (Collection[Z], List[Choice])] = {
    for {
      permutationRecord <- Ref.make(permutation)
      replayingNextIntBounded = (upper: Int) =>
        for {
          record <- permutationRecord.get
          result <- record match {
            case (recordedUpper, actual) :: tail if upper == recordedUpper =>
              permutationRecord.update(_ => tail) *> ZIO.succeed(actual)
            case (recordedUpper, _) :: _ =>
              ZIO.fail(UnmatchedPermutationDetails(calledWith = upper, expected = recordedUpper))
            case Nil => ZIO.fail(MissingPermutationDetails(calledWith = upper))
          }
        } yield result
      shuffled <- internalUsingNextIntBounded(in, replayingNextIntBounded)
      remainingRecord <- permutationRecord.get
    } yield {
      (shuffled, remainingRecord)
    }
  }

  def internalUsingNextIntBounded[Z, Collection[+Element] <: Iterable[Element], E](
      in: Collection[Z], nextIntBounded: Int => IO[E, Int])(
      implicit cbf: CanBuildFrom[Collection[Z], Z, Collection[Z]])
  : IO[E, Collection[Z]] = {
    for {
      bufferRef <- Ref.make(new mutable.ArrayBuffer[Z])
      _         <- bufferRef.update(_ ++= in)
      swap = (i1: Int, i2: Int) =>
        bufferRef.update { buffer =>
          val tmp = buffer(i1)
          buffer(i1) = buffer(i2)
          buffer(i2) = tmp
          buffer
        }
      _ <- ZIO.foreach((in.size to 2 by -1).toList)((n: Int) =>
        nextIntBounded(n).flatMap(k => swap(n - 1, k))
      )
      buffer <- bufferRef.get
    } yield {
      //bf.fromSpecific(in)(buffer)
      //cbf(in).++=(buffer).result()
      buffer.to[Collection]
    }
  }
}