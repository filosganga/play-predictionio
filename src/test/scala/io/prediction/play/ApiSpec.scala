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
import org.specs2.specification.Scope
import org.specs2.mock.Mockito
import io.prediction._
import scala.Some
import org.joda.time.DateTime


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class ApiSpec extends Specification with Mockito {


  "createUser" should {
    "call client.createUser" in new SpecsScope {
      toTest.createUser("7")

      there was one(client).createUser(any[CreateUserRequestBuilder])
    }
    "call client.getCreateUserRequestBuilder" in {
      "with given user id" in new SpecsScope {

        toTest.createUser("7")

        there was one(client).getCreateUserRequestBuilder(===("7"))
      }
      "with given location" in new SpecsScope {

        toTest.createUser("7", Some(Location(12.5, 25.1)))

        val builder = capture[CreateUserRequestBuilder]

        there was one(client).createUser(builder)

        there was builder.value.latitude(25.1)
        there was builder.value.longitude(12.5)
      }
    }
  }

  "getUser" should {
    "call client.getUser" in {
      "with given user id" in new SpecsScope {

        toTest.getUser("7")

        there was one(client).getUser("7")
      }
    }
    "return existing user" in new SpecsScope {

      val user = givenUser(new User("7"))
      val testValue = toTest.getUser("7")

      testValue shouldEqual user
    }
  }

  "deleteUser" should {
    "call client.deleteUser" in new SpecsScope {

      toTest.deleteUser("7")

      there was one(client).deleteUser("7")
    }
  }

  "createItem" should {
    "call client.createItem" in new SpecsScope {

      toTest.createItem("7")

      there was one(client).createItem(any[CreateItemRequestBuilder])
    }
    "call client.getCreateItemRequestBuilder" in {
      "the given item id" in new SpecsScope {

        toTest.createItem("7")

        there was one(client).getCreateItemRequestBuilder(===("7"), any[Array[String]])
      }
      "the given item types" in new SpecsScope {

        toTest.createItem("7", Set("foo", "bar"))

        there was one(client).getCreateItemRequestBuilder(anyString, ===(Array("foo", "bar")))
      }
      "the given location" in new SpecsScope {

        toTest.createItem("7", location = Some(Location(12.5, 25.1)))

        val builder = capture[CreateItemRequestBuilder]

        there was one(client).createItem(builder)

        there was builder.value.latitude(25.1)
        there was builder.value.longitude(12.5)

      }
      "the given start" in new SpecsScope {

        val expected = new DateTime(1985, 10, 10, 12, 30)

        toTest.createItem("7", start = Some(expected))

        val builder = capture[CreateItemRequestBuilder]

        there was one(client).createItem(builder)

        there was builder.value.startT(expected)

      }
      "the given end" in new SpecsScope {

        val expected = new DateTime(1985, 10, 10, 12, 30)

        toTest.createItem("7", end = Some(expected))

        val builder = capture[CreateItemRequestBuilder]

        there was one(client).createItem(builder)

        there was builder.value.endT(expected)

      }
    }
  }

  "getItem" should {
    "call client.getItem" in {
      "with the given item id" in new SpecsScope {

        toTest.getItem("7")

        there was one(client).getItem("7")
      }
    }
    "return a existing item" in new SpecsScope {

      val item = givenItem(new Item("7", Seq.empty[String].toArray))
      val testValue = toTest.getItem("7")

      testValue shouldEqual item
    }
  }

  "deleteItem" should {
    "call client.deleteItem" in {
      "with the given item id" in new SpecsScope {

        toTest.deleteItem("7")

        there was one(client).deleteItem("7")
      }
    }
  }

  "userActionItem" should {
    "call client.getUserActionItemRequestBuilder" in {
      "with given user id, item id and action" in new SpecsScope {

        toTest.userActionItem("1", "2", "like")

        there was one(client).getUserActionItemRequestBuilder(===("1"), ===("2"), ===("like"))
      }
      "with given rate" in new SpecsScope {

        toTest.userActionItem("1", "2", "like", Some(2))

        val builder = capture[UserActionItemRequestBuilder]

        there was one(client).userActionItem(builder)

        there was builder.value.rate(2)
      }
      "with given dateTime" in new SpecsScope {

        val expected = new DateTime(1985, 10, 10, 12, 30)

        toTest.userActionItem("1", "2", "like", dateTime = expected)

        val builder = capture[UserActionItemRequestBuilder]

        there was one(client).userActionItem(builder)

        there was builder.value.t(expected)
      }
      "with given location" in new SpecsScope {

        toTest.userActionItem("1", "2", "like", location = Some(Location(12.5, 25.1)))

        val builder = capture[UserActionItemRequestBuilder]

        there was one(client).userActionItem(builder)

        there was builder.value.longitude(12.5)
        there was builder.value.latitude(25.1)
      }
    }
  }

  "getItemsRecTopN" should {
    "call client.getItemRecGetTopNRequestBuilder" in {
      "with given engine, uid and limit" in new SpecsScope {

        toTest.getItemsRecTopN("test", "7", 50)

        there were one(client).getItemRecGetTopNRequestBuilder("test", "7", 50)

      }
      "with given types" in new SpecsScope {

        toTest.getItemsRecTopN("test", "7", 50, Set("one", "two"))

        val builder = capture[ItemRecGetTopNRequestBuilder]

        there were one(client).getItemRecTopNWithAttributes(builder)

        there were builder.value.itypes(Array("one", "two"))

      }
      "with given types" in new SpecsScope {

        toTest.getItemsRecTopN("test", "7", 50, attributes = Set("one", "two"))

        val builder = capture[ItemRecGetTopNRequestBuilder]

        there were one(client).getItemRecTopNWithAttributes(builder)

        there were builder.value.attributes(Array("one", "two"))

      }
      "with given location" in new SpecsScope {

        toTest.getItemsRecTopN("test", "7", 50, location = Some(Location(34.5, 15.7)))

        val builder = capture[ItemRecGetTopNRequestBuilder]

        there were one(client).getItemRecTopNWithAttributes(builder)

        there were builder.value.longitude(34.5)
        there were builder.value.latitude(15.7)
      }
      "with given distance" in {
        "in Km" in new SpecsScope {

          toTest.getItemsRecTopN("test", "7", 50, distance = Some(Km(10)))

          val builder = capture[ItemRecGetTopNRequestBuilder]

          there were one(client).getItemRecTopNWithAttributes(builder)

          there were builder.value.within(10D)
          there were builder.value.unit("Km")
        }
        "in Mi" in new SpecsScope {

          toTest.getItemsRecTopN("test", "7", 50, distance = Some(Mi(10)))

          val builder = capture[ItemRecGetTopNRequestBuilder]

          there were one(client).getItemRecTopNWithAttributes(builder)

          there were builder.value.within(10D)
          there were builder.value.unit("Mi")
        }
      }
    }
  }

  "getItemsSimTopN" should {
    "call client.getItemSimGetTopNRequestBuilder" in {
      "with given engine, uid and limit" in new SpecsScope {

        toTest.getItemsSimTopN("test", "7", 50)

        there were one(client).getItemSimGetTopNRequestBuilder("test", "7", 50)

      }
      "with given types" in new SpecsScope {

        toTest.getItemsSimTopN("test", "7", 50, Set("one", "two"))

        val builder = capture[ItemSimGetTopNRequestBuilder]

        there were one(client).getItemSimTopNWithAttributes(builder)

        there were builder.value.itypes(Array("one", "two"))

      }
      "with given types" in new SpecsScope {

        toTest.getItemsSimTopN("test", "7", 50, attributes = Set("one", "two"))

        val builder = capture[ItemSimGetTopNRequestBuilder]

        there were one(client).getItemSimTopNWithAttributes(builder)

        there were builder.value.attributes(Array("one", "two"))

      }
      "with given location" in new SpecsScope {

        toTest.getItemsSimTopN("test", "7", 50, location = Some(Location(34.5, 15.7)))

        val builder = capture[ItemSimGetTopNRequestBuilder]

        there were one(client).getItemSimTopNWithAttributes(builder)

        there were builder.value.longitude(34.5)
        there were builder.value.latitude(15.7)
      }
      "with given distance" in {
        "in Km" in new SpecsScope {

          toTest.getItemsSimTopN("test", "7", 50, distance = Some(Km(10)))

          val builder = capture[ItemSimGetTopNRequestBuilder]

          there were one(client).getItemSimTopNWithAttributes(builder)

          there were builder.value.within(10D)
          there were builder.value.unit("Km")
        }
        "in Mi" in new SpecsScope {

          toTest.getItemsSimTopN("test", "7", 50, distance = Some(Mi(10)))

          val builder = capture[ItemSimGetTopNRequestBuilder]

          there were one(client).getItemSimTopNWithAttributes(builder)

          there were builder.value.within(10D)
          there were builder.value.unit("Mi")
        }
      }
    }
  }




  trait SpecsScope extends Scope {

    val client = mock[Client]
    client.getCreateUserRequestBuilder(anyString) returns mockCreateUserRequestBuilder
    client.getCreateItemRequestBuilder(anyString, any) returns mockCreateItemRequestBuilder
    client.getUserActionItemRequestBuilder(anyString, anyString, anyString) returns mockUserActionItemRequestBuilder
    client.getItemRecGetTopNRequestBuilder(anyString, anyString, anyInt) returns mockItemRecGetTopNRequestBuilder
    client.getItemSimGetTopNRequestBuilder(anyString, anyString, anyInt) returns mockItemSimGetTopNRequestBuilder

    lazy val toTest = new Api {

      override def withClient[T](f: (Client) => T): T = f(client)
    }

    def givenUser(user: User) = {

      client.getUser(user.getUid) returns user
      user
    }

    def givenItem(item: Item) = {

      client.getItem(item.getIid) returns item
      item
    }

    def mockCreateUserRequestBuilder = {
      val m = mock[CreateUserRequestBuilder]

      m.latitude(anyDouble) returns m
      m.longitude(anyDouble) returns m

      m
    }

    def mockCreateItemRequestBuilder = {
      val m = mock[CreateItemRequestBuilder]

      m.latitude(anyDouble) returns m
      m.longitude(anyDouble) returns m
      m.attribute(anyString, anyString) returns m
      m.endT(any[DateTime]) returns m
      m.startT(any[DateTime]) returns m

      m
    }

    def mockUserActionItemRequestBuilder = {
      val m = mock[UserActionItemRequestBuilder]

      m.latitude(anyDouble) returns m
      m.longitude(anyDouble) returns m
      m.rate(anyInt) returns m
      m.t(any[DateTime]) returns m

      m
    }

    def mockItemRecGetTopNRequestBuilder = {

      val m = mock[ItemRecGetTopNRequestBuilder]

      m.attributes(any[Array[String]]) returns m
      m.itypes(any[Array[String]]) returns m
      m.latitude(anyDouble) returns m
      m.longitude(anyDouble) returns m
      m.unit(anyString) returns m
      m.within(anyDouble) returns m

      m
    }

    def mockItemSimGetTopNRequestBuilder = {

      val m = mock[ItemSimGetTopNRequestBuilder]

      m.attributes(any[Array[String]]) returns m
      m.itypes(any[Array[String]]) returns m
      m.latitude(anyDouble) returns m
      m.longitude(anyDouble) returns m
      m.unit(anyString) returns m
      m.within(anyDouble) returns m

      m
    }

  }

}
