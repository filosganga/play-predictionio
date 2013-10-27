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


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class ApiSpec extends Specification with Mockito {


  "API" should {
    "createUser" should {
      "call client.createUser" in new SpecsScope {

        toTest.createUser("7", Some(Location(12.6, 56.4)))

        there was one(client).createUser(any[CreateUserRequestBuilder])
      }
    }
    "getUser" should {
      "call client.getUser" in new SpecsScope {

        toTest.getUser("7")

        there was one(client).getUser("7")
      }
      "return right user" in new SpecsScope {

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
    }
    "getItem" should {
      "call client.getItem" in new SpecsScope {

        toTest.getItem("7")

        there was one(client).getItem("7")
      }
      "return right item" in new SpecsScope {

        val item = givenItem(new Item("7", Seq.empty[String].toArray))
        val testValue = toTest.getItem("7")

        testValue shouldEqual item
      }
    }
    "deleteItem" should {
      "call client.deleteItem" in new SpecsScope {

        toTest.deleteItem("7")

        there was one(client).deleteItem("7")
      }
    }
  }

  trait SpecsScope extends Scope {

    val client = mock[Client]
    client.getCreateUserRequestBuilder(anyString) answers (i => new CreateUserRequestBuilder("", "json", "", i.toString))
    client.getCreateItemRequestBuilder(anyString, any) answers ((i, types) => new CreateItemRequestBuilder("", "json", "", i.toString, Array.empty[String]))


    lazy val toTest = new Api with ClientProvider {

      def withClient[T](f: (Client) => T): T = f(client)

      def shutdown() {}
    }

    def givenUser(user: User) = {

      client.getUser(user.getUid) returns user
      user
    }

    def givenItem(item: Item) = {

      client.getItem(item.getIid) returns item
      item
    }

  }

}
