import java.util.concurrent.TimeUnit

import zio.clock.{Clock, currentTime}
import zio.duration.Duration
import zio.{Ref, ZIO}
import zio.duration._


trait TokenProvider {
  def tokenProvider: TokenProvider.Service
}

object TokenProvider {

  case class Token(i: Int)

  trait Service {
    def getToken: ZIO[Any, Throwable, Token]
  }

  trait Live extends TokenProvider {
    val tokenRef: Ref[Option[Token]]

    def tokenProvider: TokenProvider.Service = new TokenProvider.Service {

      override def getToken: ZIO[Any, Throwable, Token] =
        refreshTokenIfNeeded.provide(Clock.Live)

      def refreshTokenIfNeeded =
        tokenRef.get >>= {tok => tok.fold(doUpdateToken)(ZIO.succeed(_)) }

      def doUpdateToken = for {
        time <- currentTime(TimeUnit.SECONDS)
        t = Token(time.toInt)
        _ <- tokenRef.set(Some(t))
        _ <- expireTokenIn(10.seconds).fork
      } yield t

      def expireTokenIn(time: Duration) =
        tokenRef.set(None).delay(time)
    }
  }

}