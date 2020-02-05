import java.util.concurrent.TimeUnit

import main._
import cats.effect.Resource
import model.User
import org.http4s.{Request, Response}
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import zio.{Runtime, ZIO}
import zio.config.{config => zioconfig}
import io.circe.generic.auto._
import zio.clock.currentTime
import zio.duration._
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
      user = User(n, "nam")
      request = Request[MIO]().withMethod(POST).withUri(u).withEntity(user)
      ans <- client.use(_.expect[User](request))
      response <- Ok("Answer: " + ans)
    } yield response
  }

  def echoUser(user: User): MIO[Response[MIO]] = {
    Ok(user)
  }

  def showToken: MIO[Response[MIO]] =
    ZIO.accessM[DynamicConfig[Any]](_.token.get) >>= {tok => Ok(tok.mkString("TOKEN <", ", ", ">"))}

  def getTokenTotal: MIO[Response[MIO]] =  refreshTokenIfNeeded *> showToken

  def doUpdateToken = for {
    time <- currentTime(TimeUnit.SECONDS)
    _ <- ZIO.accessM[DynamicConfig[Any]](_.token.set(Some(time.toInt)))
    _ <- expireTokenIn(10.seconds).fork
  } yield ()

  def expireTokenIn(time: Duration) =
    ZIO.accessM[DynamicConfig[Any]](_.token.set(None)).delay(time)

  def refreshTokenIfNeeded: MIO[Unit] =
    ZIO.accessM[DynamicConfig[Any]](_.token.get) >>= {tok => doUpdateToken.when(tok.isEmpty)}

}
