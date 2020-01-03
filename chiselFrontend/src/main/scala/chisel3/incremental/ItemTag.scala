package chisel3.incremental

import java.io.File

import chisel3.experimental.BaseModule

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait UntypedTag {
  val chiselClassName: String
  val parameters: Seq[Any]

  def tag: String = parameters.hashCode().toHexString

  def tagFileName: String = chiselClassName + "." + tag + ".tag"

  def itemFileName: String = chiselClassName + "." + tag + ".item"

  private[chisel3] def store(directory: String, module: BaseModule): Unit = {
    Stash.store(new File(directory + itemFileName), module)
    Stash.store(new File(directory + tagFileName), this)
  }

  private[chisel3] def untypedLoad(directory: String): Option[BaseModule] = {
    Stash.load[BaseModule](new File(directory + itemFileName))
  }

}

/** An elaboration-agnostic tag for an elaborated module
  *
  * Used to import previously elaborated modules into a new elaboration
  *
  * @param parameters
  * @tparam T
  */
case class ItemTag[T <: BaseModule](parameters: Seq[Any])(implicit classTag: ClassTag[T]) extends UntypedTag {
  val chiselClass = classTag.runtimeClass.asInstanceOf[Class[T]]
  val chiselClassName = chiselClass.getName

  private[chisel3] def load(directory: String): Option[T] = {
    Stash.load(new File(directory + itemFileName)).asInstanceOf[Option[T]]
  }
}
