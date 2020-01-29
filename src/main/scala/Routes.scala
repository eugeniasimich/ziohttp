import main.MIO
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.implicits._
import zio.interop.catz._
import model.User
import io.circe.generic.auto._

class Routes(controller: Controller) {

  val dsl = new Http4sDsl[MIO]{}
  import dsl._
  import controller._

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
