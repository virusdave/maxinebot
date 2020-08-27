package virusdave.zio.iterable

import zio.console._
import zio.test._
import zio.test.Assertion._

object ShuffleTest extends DefaultRunnableSpec {
  //noinspection TypeAnnotation
  override def spec = suite("Shuffle") (
    testM("shuffling doesn't lose elements")(
      checkM(Gen.listOf(Gen.anyInt)) { in =>
        for {
          (out, _) <- Shuffle.capturing(in)
          //_ <- putStrLn("test")
        } yield {
          assert(out)(hasSameElements(in))
        }
      }
    ),
    testM("distinct shuffles of distinct elements are distinct")(
      checkM(Gen.listOf(Gen.anyInt)) { in =>
        val distinct = in.distinct
        for {
          (out1, perm1) <- Shuffle.capturing(distinct)
          (out2, perm2) <- Shuffle.capturing(distinct)
        } yield {
          assert(perm1)(equalTo(perm2)) ||
          assert(out1)(not(equalTo(out2)))
        }
      }
    ),
    testM("shuffles are reproducible")(
      checkM(Gen.listOf(Gen.anyInt)) { in =>
        for {
          (out1, perm1) <- Shuffle.capturing(in)
          o2 <- Shuffle.replaying(in, perm1)
          (out2, rest) = o2
        } yield {
          assert(out2)(equalTo(out1))
          assert(rest)(isEmpty)
        }
      }
    ),
  )
}