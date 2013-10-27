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
class PredictionIoPlugin(app: Application) extends Plugin {

  private lazy val config = app.configuration.getConfig("prediction")
    .getOrElse(throw app.configuration.reportError("prediction", "prediction is required"))

  protected lazy val predictionIoApi: Api with ClientProvider = new Api with DefaultClientProvider {
    protected def cfg: Configuration = config
  }

  def api: Api = predictionIoApi

  override def onStart() {
    // To init lazy field
    api

    Logger.info("PredictionIO Plugin started.")
  }

  override def onStop() {

    predictionIoApi.shutdown()

    Logger.info("PredictionIO Plugin stopped.")

  }

}



trait DefaultClientProvider extends ClientProvider {

  protected def cfg: Configuration

  private lazy val clientPool: Pool[Client] = initClientPool().get

  private def initClientPool() = {
    for {
      appKey <- cfg.getString("app-key").orElse(throw cfg.reportError("app-key", "app-key is required"))
      uri <- cfg.getString("uri").orElse(throw cfg.reportError("uri", "uri is required"))
      threadLimit <- cfg.getInt("thread-limit").orElse(Some(100))
    } yield new Pool[Client](5, threadLimit, 5 seconds)(()=>createClient(appKey, uri, 1), closeClient)
  }


  def withClient[T](f: (Client) => T): T = clientPool(f)

  def shutdown() {
    clientPool.shutdown()
  }

  protected def createClient(appKey: String, uri: String, threadLimit: Int): Client = {
    new Client(appKey, uri, threadLimit)
  }

  protected def closeClient(client: Client) {
    client.close()
  }


}
