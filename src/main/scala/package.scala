import zio.ZIO
import zio.clock.Clock
import zio.console.Console
import zio.system.System

package object main {

  type Env = Clock with Console with System
  type MIO[A] = ZIO[Env, Throwable, A]

}