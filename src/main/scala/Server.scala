import main.{Env, MIO}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext.global

object Server extends App {

  def server: MIO[Unit] = for {
    implicit0(rt: Runtime[Env]) <- ZIO.runtime[Env]
    client = BlazeClientBuilder[MIO](global).resource
    s <- BlazeServerBuilder[MIO]
      .bindHttp(8089, "localhost")
      .withHttpApp(new Routes(client).routes)
      .serve
      .compile
      .drain
  } yield s

  def run(args: List[String]) =
    server.fold(_ => 1, _ => 0)
}