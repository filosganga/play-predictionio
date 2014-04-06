package com.github.filosganga.play.predictionio

import org.specs2.specification.Scope
import org.specs2.mutable._
import org.specs2.mock.Mockito

import play.api.test._
import play.api.test.Helpers._
import org.joda.time.DateTime

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class PredictionIoSpecs extends Specification with Mockito {

//  import scala.concurrent.ExecutionContext.Implicits.global
//
//
//  "createUser" should {
//    "call api.createUser" in {
//      "with given user id" in new ThisScope {
//        running(application) {
//
//          PredictionIO.createUser("7")
//
//          there were api.createUser("7")
//        }
//      }
//      "with given location" in new ThisScope {
//        running(application) {
//
//          PredictionIO.createUser("7", location = Some(Location(9.3,7.5)))
//
//          there were api.createUser("7", Some(Location(9.3,7.5)))
//        }
//      }
//    }
//  }
//
//  "createItem" should {
//    "call api.createItem" in {
//      "with given item id" in new ThisScope {
//        running(application) {
//
//          PredictionIO.createItem("7")
//
//          there were api.createItem("7")
//        }
//      }
//      "with given types" in new ThisScope {
//        running(application) {
//
//          PredictionIO.createItem("7", types = Set("one", "two"))
//
//          there were api.createItem("7", types = Set("one", "two"))
//        }
//      }
//      "with given start" in new ThisScope {
//        running(application) {
//
//          val expected = DateTime.now()
//
//          PredictionIO.createItem("7", start = Some(expected))
//
//          there were api.createItem("7", start = Some(expected))
//        }
//      }
//      "with given end" in new ThisScope {
//        running(application) {
//
//          val expected = DateTime.now()
//
//          PredictionIO.createItem("7", end = Some(expected))
//
//          there were api.createItem("7", end = Some(expected))
//        }
//      }
//      "with given location" in new ThisScope {
//        running(application) {
//
//          PredictionIO.createItem("7", location = Some(Location(9.3,7.5)))
//
//          there were api.createItem("7", location = Some(Location(9.3,7.5)))
//        }
//      }
//    }
//  }
//
//  trait ThisScope extends Scope {
//
//    implicit val application = new FakeApplication(
//      additionalPlugins = Seq("com.github.filosganga.play.predictionio.MockedHasApi")
//    )
//
//    lazy val api: Api = application.plugin[MockedHasApi].map(_.api).getOrElse(throw new IllegalStateException("MockedHasApi not registered"))
//
//  }

}
