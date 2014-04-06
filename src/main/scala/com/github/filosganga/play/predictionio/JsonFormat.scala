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

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime

/**
 *
 * @author Filippo De Luca - fdeluca@expedia.com
 */
class JsonFormat(appKey: String) {

  val customsReads: Reads[Map[String, String]] = Reads {
    case JsObject(fields) =>
      JsSuccess(fields.collect {
        case (key, JsString(value)) if !key.startsWith("pio_") => (key, value)
      }.toMap)
  }

  implicit val locationReads: Reads[Location] = Reads {
    json => json.validate[List[Double]].map {
      case lat :: lon :: Nil => Location(lat, lon)
    }
  }

  implicit val locationWrites: Writes[Location] = Writes {
    location =>
      JsString(s"${location.latitude},${location.longitude}")
  }

  implicit val userReads: Reads[User] = (
    (__ \ "pio_uid").read[String] and
      (__ \ "pio_inactive").readNullable[Boolean]
        .map(_.getOrElse(false))
        .map(!_) and
      (__ \ "pio_latlng").readNullable[Location] and
      __.read(implicitly(customsReads))
    )(User.apply _)

  implicit val userWrites: Writes[User] = (
    (__ \ "pio_uid").write[String] and
      (__ \ "pio_appkey").write[String] and
      (__ \ "pio_inactive").write[Boolean].contramap {
        x: Boolean => !x
      } and
      (__ \ "pio_latlng").writeNullable[Location] and
      __.write[Map[String, String]]
    )(user => (user.uid, appKey, user.active, user.location, user.customs))

  implicit val itemReads: Reads[Item] = (
    (__ \ "pio_iid").read[String] and
      (__ \ "pio_itypes").readNullable[Set[String]].map {
        case Some(xs) => xs
        case _ => Set.empty[String]
      } and
      (__ \ "pio_inactive").readNullable[Boolean]
        .map(_.getOrElse(false))
        .map(!_) and
      (__ \ "pio_latlng").readNullable[Location] and
      (__ \ "pio_startT").readNullable[DateTime] and
      (__ \ "pio_endT").readNullable[DateTime] and
      (__ \ "pio_price").readNullable[Double] and
      (__ \ "pio_profit").readNullable[Double] and
      __.read(implicitly(customsReads))
    )(Item.apply _)

  implicit val itemWrites: Writes[Item] = (
    (__ \ "pio_iid").write[String] and
      (__ \ "pio_appkey").write[String] and
      (__ \ "pio_itypes").write[String] and
      (__ \ "pio_inactive").write[Boolean] and
      (__ \ "pio_latlng").writeNullable[Location] and
      (__ \ "pio_startT").writeNullable[DateTime] and
      (__ \ "pio_endT").writeNullable[DateTime] and
      (__ \ "pio_price").writeNullable[Double] and
      (__ \ "pio_profit").writeNullable[Double] and
      __.write[Map[String, String]]
    )(item => (
    item.iid,
    appKey,
    item.types.mkString(","),
    !item.active,
    item.location,
    item.startTime,
    item.endTime,
    item.price,
    item.profit,
    item.customs
    ))

  implicit val actionWrites: Writes[Action] = (
    (__ \ "pio_appkey").write[String] and
      (__ \ "pio_uid").write[String] and
      (__ \ "pio_iid").write[String] and
      (__ \ "pio_action").write[String] and
      (__ \ "pio_rate").writeNullable[Int] and
      (__ \ "pio_latlng").writeNullable[Location] and
      (__ \ "pio_t").writeNullable[DateTime] and
      __.write[Map[String, String]]
    )(action => (appKey, action.userId, action.itemId, action.action, action.rate, action.location, action.time, action.customs))

  implicit val itemInfoRead: Reads[ItemInfo] = Reads {
    case JsString(value) => JsSuccess(ItemInfo(value, Set.empty))
    case JsObject((id, JsArray(types)) :: Nil) => JsSuccess(ItemInfo(id, types.collect {
      case JsString(x) => x
    }.toSet))
    case _ => JsError("Error parsing ItemInfo")
  }
}
