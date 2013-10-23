package io.prediction.play

import org.specs2.mutable.Specification
import play.api.test.FakeApplication
import play.api.test.Helpers._
import org.specs2.specification.Scope
import play.api.{Play, Application}
import org.specs2.mock.Mockito
import io.prediction.CreateUserRequestBuilder
import scala.concurrent.{Await}
import scala.concurrent.duration._

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class PredictionIoPluginSpec extends Specification with Mockito {

  protected def fakeApp() = {

    val additionalPlugins = Seq(
      classOf[MockedPredictionIoPlugin].getName
    )

    val additionalConf = Map(
      "prediction.app-key" -> "foo",
      "prediction.uri" -> "http://locahost:8000",
      "prediction.thread-limit" -> 100,
      "prediction.pool-initial-size" -> 1,
      "prediction.pool-max-size" -> 1
    )

    FakeApplication(additionalPlugins = additionalPlugins, additionalConfiguration = additionalConf)
  }

  "PredictionIoPlugin" should {
    "provide a PredictionIO client on start" in new SpecsScope {
      running(app) {

        implicit val a = Play.current

        val p = PredictionIO.createUser("123")
        Await.result(p.future, Duration(5, SECONDS))

        there was one(client).createUser(any[CreateUserRequestBuilder])
      }
    }
  }

  trait SpecsScope extends Scope {

    val app = fakeApp()

    def client(implicit a: Application) = a.plugin[MockedPredictionIoPlugin] match {
      case Some(x) => x.mockedClient
      case _ => throw new RuntimeException("No plugin")
    }
  }

}
