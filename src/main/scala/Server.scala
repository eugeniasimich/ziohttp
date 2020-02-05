import main.{Env, MIO, Live, mconfig, MConfig}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.interop.catz._
import zio.config.Config
import zio.config.{config => zioconfig}
import scala.concurrent.ExecutionContext.global


object Server extends App {

  def server: MIO[Unit] = for {
    implicit0(rt: Runtime[Env]) <- ZIO.runtime[Env]
    conf  <- zioconfig[MConfig]
    client = BlazeClientBuilder[MIO](global).resource
    s <- BlazeServerBuilder[MIO]
      .bindHttp(conf.port, conf.host)
      .withHttpApp(new Routes(new Controller(client)).routes)
      .serve
      .compile
      .drain
  } yield s


  def run(args: List[String]) = for {
    configEnv <- Config.fromMap(Map("HOST" -> "localhost", "PORT" -> "8089", "DB_URL" -> "localhost"), mconfig)
        .orDieWith(_ => new Error("Config Error"))
    tokenRef <- Ref.make[Option[Int]](None)
    x <- server.provide(Live(configEnv.config, tokenRef)).fold(_ => 1, _ => 0)
  } yield x

}