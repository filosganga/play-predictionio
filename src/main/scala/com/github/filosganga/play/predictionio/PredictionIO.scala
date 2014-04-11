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

import play.api.Application

import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.immutable
/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
object PredictionIO {

  /** The exception we are throwing. */
  private val pluginNotRegisteredError = new IllegalStateException("PredictionIO plugin is not registered.")

  private def api(implicit app: Application): Api =
    app.plugin[HasApi].getOrElse(throw pluginNotRegisteredError).api

  def createUser(uid: UserId,
                 active: Boolean = true,
                 location: Option[Location] = None,
                 customs: Map[String, String] = Map.empty)
                (implicit app: Application, ec: ExecutionContext): Future[User] = {
    createUser(User(uid, active, location, customs))
  }

  def createUser(user: User)(implicit app: Application, ec: ExecutionContext): Future[User] = {
    api.createUser(user)
  }

  def getUser(uid: UserId)(implicit app: Application, ec: ExecutionContext): Future[User] = {
    api.getUser(uid)
  }

  def deleteUser(uid: UserId)(implicit app: Application, ec: ExecutionContext): Future[Unit] = {
    api.deleteUser(uid)
  }

  def createItem(id: ItemId,
                 types: Set[String],
                 active: Boolean = true,
                 location: Option[Location] = None,
                 start: Option[DateTime] = None,
                 end: Option[DateTime] = None,
                 price: Option[Double] = None,
                 profit: Option[Double] = None,
                 customs: Map[String, String] = Map.empty)
                (implicit app: Application, ec: ExecutionContext): Future[Item] = {
    createItem(Item(id, types, active, location, start, end, price, profit, customs))
  }

  def createItem(item: Item)(implicit app: Application, ec: ExecutionContext): Future[Item] = {
    api.createItem(item)
  }

  def getItem(id: ItemId)(implicit app: Application, ec: ExecutionContext): Future[Item] =
    api.getItem(id)

  def deleteItem(id: ItemId)(implicit app: Application, ec: ExecutionContext): Future[Unit] = {
    api.deleteItem(id)
  }

  def userActionItem(userId: UserId,
                     itemId: ItemId,
                     action: U2IAction,
                     rate: Option[Int] = None,
                     dateTime: Option[DateTime] = None,
                     location: Option[Location] = None,
                     customs: Map[String, String] = Map.empty)
                    (implicit app: Application, ec: ExecutionContext): Future[Action] = {

    api.userAction(Action(userId, itemId, action, rate, location, dateTime, customs))
  }

  def userActionItem(action: Action)(implicit app: Application, ec: ExecutionContext): Future[Action] = {
    api.userAction(action)
  }

  def getItemsInfoRecTopN(engine: String,
                          userId: UserId,
                          n: Int = 15,
                          types: Set[String] = Set.empty,
                          attributes: Set[String] = Set.empty,
                          location: Option[Location],
                          distance: Option[Distance])(implicit app: Application, ec: ExecutionContext): Future[immutable.Seq[ItemInfo]] = {

    api.getItemsRecTopN(engine, userId, n, types, attributes, location, distance)
  }

  def getItemsInfoSimTopN(engine: String,
                          targetId: ItemId,
                          n: Int = 15,
                          types: Set[String] = Set.empty,
                          attributes: Set[String] = Set.empty,
                          location: Option[Location],
                          distance: Option[Distance])(implicit app: Application, ec: ExecutionContext): Future[immutable.Seq[ItemInfo]] = {

    api.getItemsSimTopN(engine, targetId, n, types, attributes, location, distance)
  }

  def getItemsRecTopN(engine: String,
                      userId: UserId,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location],
                      distance: Option[Distance])(implicit app: Application, ec: ExecutionContext): Future[immutable.Seq[Item]] = {

    getItemsInfoRecTopN(engine, userId, n, types, attributes, location, distance).flatMap {
      xs => Future.sequence(xs.map(x => getItem(x.id)))
    }
  }

  def getItemsSimTopN(engine: String,
                      targetId: ItemId,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location],
                      distance: Option[Distance])(implicit app: Application, ec: ExecutionContext): Future[immutable.Seq[Item]] = {

    getItemsInfoSimTopN(engine, targetId, n, types, attributes, location, distance).flatMap {
      xs => Future.sequence(xs.map(x => getItem(x.id)))
    }
  }

}
