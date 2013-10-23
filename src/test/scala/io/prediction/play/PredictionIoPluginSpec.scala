/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
      "prediction.uri" -> "http://localhost:8000",
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
