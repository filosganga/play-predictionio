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
  implicit val u2iActionFormat = new Format[U2IAction] {
    def reads(json: JsValue): JsResult[U2IAction] = json.validate[String] map {
      case "like" => U2IAction.Like
      case "dislike" => U2IAction.Dislike
      case "rate" => U2IAction.Rate
      case "view" => U2IAction.View
      case "viewDetails" => U2IAction.ViewDetails
      case "conversion" => U2IAction.Conversion
    }
    def writes(o: U2IAction): JsValue = JsString(o.toString.toLowerCase)
  }

  implicit val userIdFormat= new Format[UserId] {
    override def writes(o: UserId): JsValue = JsString(o.value)
    override def reads(json: JsValue): JsResult[UserId] = json.validate[String].map(UserId.apply)
  }

  implicit val itemIdFormat= new Format[ItemId] {
    override def writes(o: ItemId): JsValue = JsString(o.value)
    override def reads(json: JsValue): JsResult[ItemId] = json.validate[String].map(ItemId.apply)
  }

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
    (__ \ "pio_uid").read[UserId] and
      (__ \ "pio_inactive").readNullable[Boolean]
        .map(_.getOrElse(false))
        .map(!_) and
      (__ \ "pio_latlng").readNullable[Location] and
      __.read(implicitly(customsReads))
    )(User.apply _)

  implicit val userWrites: Writes[User] = (
    (__ \ "pio_uid").write[UserId] and
      (__ \ "pio_appkey").write[String] and
      (__ \ "pio_inactive").write[Boolean].contramap {
        x: Boolean => !x
      } and
      (__ \ "pio_latlng").writeNullable[Location] and
      __.write[Map[String, String]]
    )(user => (user.uid, appKey, user.active, user.location, user.customs))

  implicit val itemReads: Reads[Item] = (
    (__ \ "pio_iid").read[ItemId] and
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
    (__ \ "pio_iid").write[ItemId] and
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
      (__ \ "pio_uid").write[UserId] and
      (__ \ "pio_iid").write[ItemId] and
      (__ \ "pio_action").write[U2IAction] and
      (__ \ "pio_rate").writeNullable[Int] and
      (__ \ "pio_latlng").writeNullable[Location] and
      (__ \ "pio_t").writeNullable[DateTime] and
      __.write[Map[String, String]]
    )(action => (appKey, action.userId, action.itemId, action.action, action.rate, action.location, action.time, action.customs))

  import scala.collection.immutable
  implicit val predictionRead:Reads[immutable.Seq[ItemInfo]] = {
    def toSeqItemInfo(ids:immutable.Seq[ItemId], attributes:Map[String,List[String]]):immutable.Seq[ItemInfo]={
      val attributesWithIds=attributes.transform((k,v)=>ids.zip(v).toMap)
      val attributesById=attributesWithIds.map{
        case (at,map)=> map.map{
          case (k,v) => k -> (at -> v)
        }}.flatten
        .groupBy{ case(k,v)=>k}
        .transform((k,v)=>v.map{case(i,j)=>j}.toMap).withDefaultValue(Map())
      ids.map(id=> ItemInfo(id, attributesById(id)))
    }
    val tuple=(
      (__ \ "pio_iids").read[immutable.Seq[ItemId]] and
        __.read(
          (__ \ "pio_iids").json.prune.map(x=>x.as[Map[String,List[String]]])
        )
      ).tupled
    tuple.map((toSeqItemInfo _).tupled.apply)
  }
}
