package com.github.filosganga.play.predictionio

import org.joda.time.DateTime

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


case class User(uid: String,
                active: Boolean = true,
                location: Option[Location] = None,
                customs: Map[String, String] = Map.empty)

case class Item(iid: String,
                types: Set[String] = Set.empty,
                active: Boolean = true,
                location: Option[Location] = None,
                startTime: Option[DateTime] = None,
                endTime: Option[DateTime] = None,
                price: Option[Double] = None,
                profit: Option[Double] = None,
                customs: Map[String, String] = Map.empty)

case class Action(userId: String,
                  itemId: String,
                  action: String,
                  rate: Option[Int] = None,
                  location: Option[Location] = None,
                  time: Option[DateTime] = None,
                  customs: Map[String, String] = Map.empty)

case class Location(longitude: Double, latitude: Double)

abstract sealed class Distance {

  def value: Double
}

case class Km(value: Double) extends Distance

case class Mi(value: Double) extends Distance