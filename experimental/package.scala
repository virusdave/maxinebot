import virusdave.ops
import virusdave.zio.{ops => zops}

package object experimental
  extends ops.AnyOps
  with ops.IterableOps
  with ops.OptionOps
  with zops.IterableOps
  with zops.OptionOps {
}
