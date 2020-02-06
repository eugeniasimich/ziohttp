import zio.{Ref, ZIO}
import zio.clock.Clock
import zio.config.{Config, ConfigDescriptor}
import ConfigDescriptor.{int, string}
import TokenProvider.Token
import io.circe.{Decoder, Encoder}
import zio.console.Console
import zio.system.System
import org.http4s.{EntityDecoder, EntityEncoder, Uri, UriTemplate}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import zio.interop.catz._

package object main {

  type Env = Clock with Console with System with Config[MConfig] with TokenProvider
  type MIO[A] = ZIO[Env, Throwable, A]


  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[MIO, A] = jsonOf[MIO, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[MIO, A] = jsonEncoderOf[MIO, A]


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

  case class Live(config: Config.Service[MConfig], tokenRef: Ref[Option[Token]])
    extends Config[MConfig]
      with Console.Live
      with Clock.Live
      with System.Live with TokenProvider.Live
}