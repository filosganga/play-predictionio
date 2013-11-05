package com.github.filosganga.play.predictionio

import io.prediction.{User, Item, Client}
import play.api.Logger
import org.joda.time.DateTime

import collection.JavaConversions._


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
trait Api {

  def getUser(id: String): User = {
    withClient(_.getUser(id))
  }

  def createUser(uid: String, location: Option[Location] = None): User =
    withClient(userFor(uid, location))

  private def userFor(uid: String, location: Option[Location])(client: Client): User = {
    client.createUser(location.foldLeft(client.getCreateUserRequestBuilder(uid))((s, x) =>
      s.latitude(x.latitude).longitude(x.longitude)
    ))

    val created = location.foldLeft(new User(uid))((s, x) => s.latitude(x.latitude).longitude(x.longitude))

    Logger.debug("Created user with uid=" + created.getUid)

    created
  }

  def deleteUser(uid: String) {
    withClient(_.deleteUser(uid))
  }

  def getItem(id: String): Item = {
    withClient(_.getItem(id))
  }


  def createItem(id: String,
                 types: Set[String] = Set.empty,
                 location: Option[Location] = None,
                 start: Option[DateTime] = None,
                 end: Option[DateTime] = None): Item = {

    withClient(itemFor(id, types, location, start, end))
  }

  private def itemFor(id: String, types: Set[String], location: Option[Location], start: Option[DateTime], end: Option[DateTime])(client: Client): Item = {

    val request = (((client.getCreateItemRequestBuilder(id, types.toArray) /: location) {
      (s, x) =>
        s.latitude(x.latitude).longitude(x.longitude)
    } /: start) {
      (s, x) =>
        s.startT(x)
    } /: end) {
      (s, x) =>
        s.endT(x)
    }

    client.createItem(request)

    val created = new Item(id, types.toArray)

    Logger.debug("Created Item with id=" + created.getIid + " and types=" + created.getItypes.mkString(","))

    created
  }

  def deleteItem(id: String) {
    withClient(_.deleteItem(id))
  }

  def userActionItem(userId: String,
                     itemId: String,
                     action: String,
                     rate: Option[Int] = None,
                     dateTime: DateTime = DateTime.now(),
                     location: Option[Location] = None) {
    withClient {
      client =>

        val request = client.getUserActionItemRequestBuilder(userId, itemId, action).t(dateTime)
        rate.foreach(request.rate)
        location.foreach(l=> request.longitude(l.longitude).latitude(l.latitude))

        client.userActionItem(request)
    }
  }

  def getItemsRecTopN(engine: String,
                      userId: String,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location] = None,
                      distance: Option[Distance] = None): Iterable[ItemInfo] = {

    withClient{client=>

      val rb = client.getItemRecGetTopNRequestBuilder(engine, userId, n)
      rb.attributes(attributes.toArray)
      rb.itypes(types.toArray)

      location.foreach(l=> rb.latitude(l.latitude).longitude(l.longitude))
      distance.foreach {
        case Km(x) => rb.unit("km").within(x)
        case Mi(x) => rb.unit("mi").within(x)
      }

      client.getItemRecTopNWithAttributes(rb)

    }.map{case (id, atts) => ItemInfo(id, attributes.toSet)}
  }

  def getItemsSimTopN(engine: String,
                      targetId: String,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location] = None,
                      distance: Option[Distance] = None): Iterable[ItemInfo] = {

    withClient{client=>

      val rb = client.getItemSimGetTopNRequestBuilder(engine, targetId, n)
      rb.attributes(attributes.toArray)
      rb.itypes(types.toArray)

      location.foreach(l=> rb.latitude(l.latitude).longitude(l.longitude))
      distance.foreach{
        case Km(x) => rb.unit("km").within(x)
        case Mi(x) => rb.unit("mi").within(x)
      }

      client.getItemSimTopNWithAttributes(rb)

    }.map{case (id, atts) => ItemInfo(id, attributes.toSet)}
  }

  protected def withClient[T](f: Client => T): T

}

abstract sealed class Distance {

  def value: Double
}

case class Km(value: Double) extends Distance

case class Mi(value: Double) extends Distance