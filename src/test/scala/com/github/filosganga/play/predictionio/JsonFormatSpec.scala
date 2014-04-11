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
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.joda.time.DateTime
import scala.collection.immutable


/**
 *
 * @author Filippo De Luca - fdeluca@expedia.com
 */
class JsonFormatSpec extends Specification {

  "userReads" should {
    "be able to read an User" in {
      "with only id" in new ThisScope {

        import jsonFormat._

        val user: User = Json.fromJson[User](Json.parse(
          """
            |{
            | "pio_uid":"test"
            | }
          """.stripMargin)).get

        user should be equalTo User(UserId("test"))
      }

      "with active" in new ThisScope {

        import jsonFormat._

        val user: User = Json.fromJson[User](Json.parse(
          """
            |{
            | "pio_uid":"test",
            | "pio_inactive": true
            | }
          """.stripMargin)).get

        user should be equalTo User(UserId("test"), active = false)
      }


      "with location" in new ThisScope {

        import jsonFormat._

        val user: User = Json.fromJson[User](Json.parse(
          """
            |{
            | "pio_uid":"test",
            | "pio_latlng":[12.34,5.67]
            | }
          """.stripMargin)).get

        user should be equalTo User(UserId("test"), location = Some(Location(12.34, 5.67)))
      }

      "with customs" in new ThisScope {

        import jsonFormat._

        val user: User = Json.fromJson[User](Json.parse(
          """
            |{
            | "pio_uid":"test",
            | "foo":"bar",
            | "aaa":"bbb"
            | }
          """.stripMargin)).get

        user should be equalTo User(UserId("test"), customs = Map("foo"->"bar", "aaa"->"bbb"))
      }

      "with all fields" in new ThisScope {

        import jsonFormat._

        val user: User = Json.fromJson[User](Json.parse(
          """
            |{
            | "pio_uid":"test",
            | "pio_inactive": true,
            | "pio_latlng":[12.34,5.67],
            | "foo":"bar",
            | "aaa":"bbb"
            | }
          """.stripMargin)).get

        user should be equalTo User(UserId("test"), active = false, location = Some(Location(12.34, 5.67)), customs = Map("foo"->"bar", "aaa"->"bbb"))
      }
    }
  }


  "userWrites" should {
    "be able to write an User" in {
      "with only id" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(User(UserId("test")))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("test")
      }

      "with active" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(User(UserId("test"), active = false))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("test")
        (json \ "pio_inactive").validate[Boolean] should be equalTo JsSuccess(true)
      }

      "with location" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(User(UserId("test"), location = Some(Location(9.4, 3.4))))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("test")
        (json \ "pio_latlng").validate[String] should be equalTo JsSuccess("3.4,9.4")
      }

      "with customs" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(User(UserId("test"), customs = Map("foo"->"Bar")))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("test")
        (json \ "foo").validate[String] should be equalTo JsSuccess("Bar")
      }

      "with everything" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(User(UserId("test"), active = false, location = Some(Location(12.3, 45.6)), customs = Map("foo"->"Bar")))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("test")
        (json \ "pio_inactive").validate[Boolean] should be equalTo JsSuccess(true)
        (json \ "pio_latlng").validate[String] should be equalTo JsSuccess("45.6,12.3")
        (json \ "foo").validate[String] should be equalTo JsSuccess("Bar")
      }
    }
  }


  "itemReads" should {
    "be able to read an Item" in {
      "with only id" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test"
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"))
      }

      "with active" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_inactive": true
            | }
          """.stripMargin)).fold(
          invalid => throw new RuntimeException(invalid.mkString(",")),
          valid => valid
        )

        item should be equalTo Item(ItemId("test"), active = false)
      }

      "with types" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_itypes":["one","two","three"]
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )
        item should be equalTo Item(ItemId("test"), types = Set("one", "two", "three"))
      }


      "with location" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_latlng":[12.34,5.67]
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )
        item should be equalTo Item(ItemId("test"), location = Some(Location(12.34, 5.67)))
      }

      "with price" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_price":123.67
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )
        item should be equalTo Item(ItemId("test"), price = Some(123.67))
      }

      "with profit" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_profit":123.67
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"), profit = Some(123.67))
      }

      "with start time" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_startT": 123456789
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"), startTime = Some(new DateTime(123456789L)))
      }

      "with end time" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_endT": 123456790
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"), endTime = Some(new DateTime(123456790L)))
      }

      "with customs" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "foo":"bar",
            | "aaa":"bbb"
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"), customs = Map("foo"->"bar", "aaa"->"bbb"))
      }

      "with all fields" in new ThisScope {

        import jsonFormat._

        val item: Item = Json.fromJson[Item](Json.parse(
          """
            |{
            | "pio_iid":"test",
            | "pio_inactive": true,
            | "pio_itypes": ["one","two","three"],
            | "pio_latlng": [12.34,5.67],
            | "pio_startT": 123456789,
            | "pio_endT": 123456790,
            | "pio_price": 321.67,
            | "pio_profit": 123.67,
            | "foo":"bar",
            | "aaa":"bbb"
            | }
          """.stripMargin)).fold(
            invalid => throw new RuntimeException(invalid.mkString(",")),
            valid => valid
          )

        item should be equalTo Item(ItemId("test"), Set("one", "two", "three"), active = false, Some(Location(12.34, 5.67)), Some(new DateTime(123456789L)), Some(new DateTime(123456790L)), Some(321.67), Some(123.67), Map("foo"->"bar", "aaa"->"bbb"))
      }
    }
  }

  "itemWrites" should {
    "be able to write an Item" in {
      "with everything" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(Item(
          iid = ItemId("test"),
          types = Set("foo", "bar"),
          active = false,
          location = Some(Location(12.3, 45.6)),
          price = Some(12.3),
          profit = Some(98.5),
          startTime = Some(new DateTime(2013, 6, 23,12,30)),
          endTime = Some(new DateTime(2013, 5, 20,12,30)),
          customs = Map("foo"->"Bar")))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_iid").validate[String] should be equalTo JsSuccess("test")
        (json \ "pio_itypes").validate[String] should be equalTo JsSuccess("foo,bar")
        (json \ "pio_inactive").validate[Boolean] should be equalTo JsSuccess(true)
        (json \ "pio_latlng").validate[String] should be equalTo JsSuccess("45.6,12.3")
        (json \ "pio_price").validate[Double] should be equalTo JsSuccess(12.3)
        (json \ "pio_profit").validate[Double] should be equalTo JsSuccess(98.5)
        (json \ "pio_startT").validate[DateTime] should be equalTo JsSuccess(new DateTime(2013, 6, 23,12,30))
        (json \ "pio_endT").validate[DateTime] should be equalTo JsSuccess(new DateTime(2013, 5, 20,12,30))
        (json \ "foo").validate[String] should be equalTo JsSuccess("Bar")
      }
    }
  }


  "actionWrites" should {
    "be able to write an Action" in {
      "with everything" in new ThisScope {

        import jsonFormat._

        val json = Json.toJson(Action(UserId("jhon"),ItemId("apple"), U2IAction.Like, Some(9), Some(Location(9.8, 34.8)), Some(new DateTime(2013, 6, 23,12,30)), Map("foo"->"bar")))

        (json \ "pio_appkey").validate[String] should be equalTo JsSuccess(appKey)
        (json \ "pio_uid").validate[String] should be equalTo JsSuccess("jhon")
        (json \ "pio_iid").validate[String] should be equalTo JsSuccess("apple")
        (json \ "pio_action").validate[String] should be equalTo JsSuccess("like")
        (json \ "pio_rate").validate[Int] should be equalTo JsSuccess(9)
        (json \ "pio_latlng").validate[String] should be equalTo JsSuccess("34.8,9.8")
        (json \ "pio_t").validate[DateTime] should be equalTo JsSuccess(new DateTime(2013, 6, 23,12,30))
        (json \ "foo").validate[String] should be equalTo JsSuccess("bar")
      }
    }
  }

  "predictionRead" should{
    "be able to read a prediction result in a Seq[ItemInfo]" in new ThisScope{
      import jsonFormat.predictionRead
      val json=Json.parse(
        """
          |{
          |  "pio_iids": [
          |  "52d970c46c00006b004ac6d3",
          |  "52de8270c30000c200c21fd3"
          |  ],
          |  "name": [
          |  "foo",
          |  "bar"
          |  ]
          |}
        """.stripMargin)
      val items=List(
        ItemInfo(ItemId("52d970c46c00006b004ac6d3"), Map("name"->"foo")),
        ItemInfo(ItemId("52de8270c30000c200c21fd3"), Map("name"->"bar"))
      )
      json.validate[immutable.Seq[ItemInfo]](predictionRead) === JsSuccess(items)
    }
  }

  trait ThisScope extends Scope {

    val appKey = "foo"

    val jsonFormat = new JsonFormat(appKey)

  }

}
