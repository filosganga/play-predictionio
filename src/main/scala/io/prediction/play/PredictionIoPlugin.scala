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

import play.api._
import io.prediction.Client
import scala.concurrent.duration._
import scala.Some


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class PredictionIoPlugin(app: Application) extends Plugin with HasApi {

  private lazy val cfg = app.configuration.getConfig("prediction")
    .getOrElse(throw app.configuration.reportError("prediction", "prediction is required"))

  private lazy val clientProvider = new ConfigClientProvider(cfg)

  private lazy val predictionIoApi = new Api {
    protected def withClient[T](f: (Client) => T): T = clientProvider.withClient(f)
  }

  def api: Api = predictionIoApi

  override def onStart() {
    // To init lazy field
    api

    Logger.info("PredictionIO Plugin started.")
  }

  override def onStop() {

    clientProvider.shutdown()

    Logger.info("PredictionIO Plugin stopped.")

  }

}



class ConfigClientProvider(cfg: Configuration) extends ClientProvider {

  private lazy val client: Client = initClient()

  private def initClient(): Client = new Client(
    cfg.getString("app-key").getOrElse(throw cfg.reportError("app-key", "app-key is required")),
    cfg.getString("uri").getOrElse(throw cfg.reportError("uri", "uri is required")),
    cfg.getInt("thread-limit").getOrElse(100)
  )

  def withClient[T](f: (Client) => T): T = {
    f(client)
  }

  def shutdown() {
    client.close()
  }

}
