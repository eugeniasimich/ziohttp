import main.{Env, MIO}
import cats.effect.Resource
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.implicits._
import zio.interop.catz._
import io.circe.{Decoder, Encoder}
import model.User
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._
import io.circe.generic.auto._

class Routes(client: Resource[MIO, Client[MIO]]) {

  val dsl = new Http4sDsl[MIO]{}
  import dsl._

  val controller = new Controller(client)
  import controller._

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[MIO, A] = jsonOf[MIO, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[MIO, A] = jsonEncoderOf[MIO, A]

  implicit def circeJsonDecoderUser(implicit decoder: Decoder[User]): EntityDecoder[MIO, User] = circeJsonDecoder[User]
  def routes: HttpApp[MIO] = HttpRoutes.of[MIO] {
    case GET -> Root => intro
    case GET -> Root / "ask" / IntVar(n) => ask(n)
    case GET -> Root / IntVar(n) => echo(n)
    case request @ POST -> Root =>
      request.decode[User] { user =>
        echoUser(user)
      }
  }.orNotFound

}
