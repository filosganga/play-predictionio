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

import play.api.{Play, Logger, Plugin, Application}
import io.prediction.Client
import scala.concurrent._
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits.defaultContext


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class PredictionIoPlugin(app: Application) extends Plugin {

  protected val logger = Logger(getClass)

  private var clientPool: Pool[Client] = null

  override def onStart() {

    val cfg = app.configuration.getConfig("prediction").getOrElse(throw new RuntimeException("prediction configuration not found"))

    val appKey = cfg.getString("app-key").getOrElse(throw new RuntimeException)
    val uri = cfg.getString("uri").getOrElse("http://localhost:8000")
    val threadLimit = cfg.getInt("thread-limit").getOrElse(100)

    clientPool = new Pool[Client](
      cfg.getInt("pool-initial-size").getOrElse(5),
      cfg.getInt("pool-max-size").getOrElse(20),
      cfg.getInt("pool-acquire-timeout").map(Duration(_, SECONDS)).getOrElse(60 seconds)
    )(() => createClient(appKey, uri, threadLimit), closeClient)
  }

  override def onStop() {

  }

  private[play] def promise[T](f: Client => T): Promise[T] = {

    val p = Promise[T]()

    clientPool {client=>
      execute(client)(f).onComplete {
        case Success(v) => p.success(v)
        case Failure(t) => p.failure(t)
      }
    }

    p
  }

  private def execute[T](clientToUse: => Client)(f: Client => T): Future[T] = future {
    f(clientToUse)
  }

  protected def createClient(appKey: String, uri: String, threadLimit: Int): Client = {
    new Client(appKey, uri, threadLimit)
  }

  protected def closeClient(client: Client) {
    client.close()
  }
}
