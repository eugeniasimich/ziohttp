import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.interop.catz._
import zio.system.System
import scala.concurrent.ExecutionContext.global

object Server extends App {

  type Env = Clock with Console with System
  type MIO[A] = ZIO[Env, Throwable, A]

  def server: MIO[Unit] = for {
    implicit0(rt: Runtime[Env]) <- ZIO.runtime[Env]
    client = BlazeClientBuilder[MIO](global).resource
    s <- BlazeServerBuilder[MIO]
      .bindHttp(8080, "localhost")
      .withHttpApp(new Routes(client).routes)
      .serve
      .compile
      .drain
  } yield s

  def run(args: List[String]) =
    server.fold(_ => 1, _ => 0)
}