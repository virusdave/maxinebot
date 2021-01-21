import virusdave.ops.{AnyOps, IterableOps, OptionOps}
import virusdave.zio.ops.{IterableOps => ZIterableOps, OptionOps => ZOptionOps}

package object maxine
  extends AnyOps
  with OptionOps
  with IterableOps
  with ZIterableOps
  with ZOptionOps { }
