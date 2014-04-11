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

package com.github.filosganga.play



/**
 *
 * @author Filippo De Luca - fdeluca@expedia.com
 */
package object predictionio {

  trait PredictionIOException {
    val message = ""
    val cause: Throwable = null
  }

  case class NoRecommendationException(override val message: String, override val cause: Throwable = null) extends Exception(message, cause, true, false) with PredictionIOException

  case class PredictionIOServerException(override val message: String, override val cause: Throwable = null) extends Exception(message, cause, true, false) with PredictionIOException

  case class PredictionIOClientException(override val message: String, override val cause: Throwable = null) extends Exception(message, cause, true, false) with PredictionIOException

}

