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

package com.github.filosganga.play.predictionio

import play.api._


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class PredictionIoPlugin(app: Application) extends Plugin with HasApi {

  private lazy val predictionIoApi = new Api with ConfigEndpointProvider {
    override def cfg: Configuration = app.configuration.getConfig("prediction-io")
      .getOrElse(throw app.configuration.reportError("prediction-io", "prediction-io is required"))
  }

  def api: Api = predictionIoApi

  override def onStart() {
    // To init lazy field
    api

    Logger.info("PredictionIO Plugin started.")
  }

  override def onStop() {

    Logger.info("PredictionIO Plugin stopped.")

  }

}



trait ConfigEndpointProvider extends EndpointProvider {

  def cfg: Configuration

  override val apiKey: String =
    cfg.getString("app-key").getOrElse(throw cfg.reportError("app-key", "app-key is required"))

  override val endpoint: String =
    cfg.getString("endpoint").getOrElse(throw cfg.reportError("endpoint", "endpoint is required"))
}
