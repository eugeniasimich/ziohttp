import zio.{ZIO, clock}
import zio.clock.Clock
import zio.config.{Config, ConfigDescriptor}
import ConfigDescriptor.{int, string}
import zio.console.Console
import zio.system.System
import org.http4s.UriTemplate
import org.http4s.Uri

package object main {

  type Env = Clock with Console with System with Config[MConfig]
  type MIO[A] = ZIO[Env, Throwable, A]

  case class MConfig(host: String, port: Int, dburl: String){
    def buildRootUri: MIO[Uri] = {
      val template = UriTemplate(
        authority = Some(Uri.Authority(host = Uri.RegName(host), port = Some(port))),
        scheme = Some(Uri.Scheme.http)
      )
      ZIO.fromTry(template.toUriIfPossible)
    }
  }
  val mconfig: ConfigDescriptor[String, String, MConfig] =
    (string("HOST") |@| int("PORT")|@| string("DB_URL"))(MConfig.apply, MConfig.unapply)

  case class Live(config: Config.Service[MConfig])
    extends Config[MConfig]
      with Console.Live
      with Clock.Live
      with System.Live
}