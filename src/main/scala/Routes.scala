import Server.MIO
import cats.effect.Resource
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.implicits._
import zio.interop.catz._

class Routes(client: Resource[MIO, Client[MIO]]) {

  val dsl = new Http4sDsl[MIO]{}
  import dsl._

  val controller = new Controller(client)
  import controller._

  def routes: HttpApp[MIO] = HttpRoutes.of[MIO] {
    case GET -> Root => intro
    case GET -> Root / "ask" / IntVar(n) => ask(n)
    case GET -> Root / IntVar(n) => echo(n)
  }.orNotFound

}
