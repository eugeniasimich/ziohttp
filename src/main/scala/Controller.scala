import main.{Env, MConfig, MIO}
import cats.effect.Resource
import model.User
import org.http4s.EntityDecoder._
import org.http4s.Response
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import zio.interop.catz._
import zio.{Runtime, ZIO}
import zio.config.{config => zioconfig}

class Controller(client: Resource[MIO, Client[MIO]]) {
  val dsl = new Http4sDsl[MIO] {}
  import dsl._
  val dslc = new Http4sClientDsl[MIO] {}
  import dslc._

  def intro: MIO[Response[MIO]] =
    Ok("Hello World!")

  def echo(n: Int): MIO[Response[MIO]] =
    Ok("Number " + n)

  def ask(n: Int): MIO[Response[MIO]] = {
    for {
      implicit0(rt: Runtime[Env]) <- ZIO.runtime[Env]
      u <- zioconfig[MConfig].flatMap(_.buildRootUri)
      ans <- client.use(_.expect[String](GET(u / n.toString)))
      response <- Ok("Answer: " + ans)
    } yield response
  }

  def echoUser(user: User): MIO[Response[MIO]] = {
    Ok("User id: " + user.id + ". User name: " +  user.name)
  }

}
