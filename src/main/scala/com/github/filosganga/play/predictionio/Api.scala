package com.github.filosganga.play
package predictionio


import scala.concurrent._
import scala.collection.immutable
import play.api.libs.ws.WS
import play.api.libs.json._


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
trait Api {
  self: EndpointProvider =>

  lazy val format = new JsonFormat(apiKey)

  def getUser(id: UserId)(implicit ec: ExecutionContext): Future[User] = {

    import format._

    WS.url(s"$endpoint/users/$id.json").withQueryString("pio_appkey" -> apiKey).get().flatMap {
      response =>
        response.status match {
          case x if x >= 300 => Future.failed(new RuntimeException(response.statusText))
          case _ =>
            Json.fromJson[User](response.json).fold(invalid => Future.failed(new RuntimeException), Future.successful)
        }
    }
  }

  def createUser(user: User)(implicit ec: ExecutionContext): Future[User] = {

    import format._

    WS.url(s"$endpoint/users.json").post(Json.toJson(user)).flatMap {
      case response if response.status >= 300 => Future.failed(new RuntimeException(response.statusText))
      case _ => Future.successful(user)
    }
  }

  def deleteUser(uid: UserId)(implicit ec: ExecutionContext): Future[Unit] = {
    WS.url(s"$endpoint/users/$uid.json").withQueryString("pio_appkey" -> apiKey).delete().map {
      case response if response.status >= 300 => Future.failed(new RuntimeException(response.statusText))
      case _ => Future.successful()
    }
  }

  def getItem(id: ItemId)(implicit ec: ExecutionContext): Future[Item] = {

    import format._

    WS.url(s"$endpoint/items/$id.json").withQueryString("pio_appkey" -> apiKey).get().flatMap {
      case response if response.status >= 300 => Future.failed(new RuntimeException(response.statusText))
      case response => Json.fromJson[Item](response.json).fold(invalid => Future.failed(new RuntimeException), Future.successful)
    }
  }

  def createItem(item: Item)(implicit ec: ExecutionContext): Future[Item] = {

    import format._

    WS.url(s"$endpoint/items.json").post(Json.toJson(item)).flatMap {
      case response if response.status >= 300 => {
        Future.failed(new RuntimeException(s"Error: ${response.statusText}, json: ${Json.stringify(Json.toJson(item))}"))
      }
      case _ => Future.successful(item)
    }
  }

  def deleteItem(id: ItemId)(implicit ec: ExecutionContext): Future[Unit] = future {
    WS.url(s"$endpoint/items/$id.json").withQueryString("pio_appkey" -> apiKey).delete().flatMap {
      case response if response.status >= 300 => Future.failed(new RuntimeException(response.statusText))
      case _ => Future.successful()
    }
  }

  def userAction(action: Action)(implicit ec: ExecutionContext): Future[Action] = {

    import format._

    WS.url(s"$endpoint/actions/u2i.json").post(Json.toJson(action)) flatMap {
      case response if response.status >= 300 => Future.failed(new RuntimeException(response.statusText))
      case _ => Future.successful(action)
    }
  }

  def getItemsRecTopN(engine: String,
                      userId: UserId,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location] = None,
                      distance: Option[Distance] = None)
                     (implicit ec: ExecutionContext): Future[immutable.Seq[ItemInfo]] = {

    import format._

    val parameters = Seq(
      "pio_appkey" -> apiKey,
      "pio_uid" -> userId.value,
      "pio_n" -> s"$n"
    ) ++
      (if (types.nonEmpty) Seq("pio_itypes" -> types.mkString(",")) else Nil) ++
      (if (attributes.nonEmpty) Seq("pio_attributes" -> attributes.mkString(",")) else Nil) ++
      location.map(x => Seq("pio_latlng" -> s"${x.latitude},${x.longitude}")).getOrElse(Nil) ++
      distance.map {
        case Km(value) => Seq("pio_within" -> s"$value", "pio_unit" -> "km")
        case Mi(value) => Seq("pio_within" -> s"$value", "pio_unit" -> "mi")
      }.getOrElse(Nil)


    lazy val queryStr= s" id($userId) in engine($engine). Searching with types [${types.mkString(",")}], attributes [${attributes.mkString(",")}}], location ($location) and distance ($distance)"
    WS.url(s"$endpoint/engines/itemrec/$engine/topn.json").withQueryString(parameters: _*).get().flatMap {
      case response if response.status == 404 && isNoRecommendationMessage(response.json)=> Future.failed(new NoRecommendationException(response.statusText+ queryStr))
      case response if response.status >= 500 => Future.failed(new PredictionIOServerException(response.body))
      case response if response.status >= 300 => Future.failed(new PredictionIOClientException(response.body))
      case response => Json.fromJson[immutable.Seq[ItemInfo]](response.json).fold(invalid => Future.failed(new PredictionIOClientException(response.body)), Future.successful)
    }
  }

  def getItemsSimTopN(engine: String,
                      targetId: ItemId,
                      n: Int = 15,
                      types: Set[String] = Set.empty,
                      attributes: Set[String] = Set.empty,
                      location: Option[Location] = None,
                      distance: Option[Distance] = None)
                     (implicit ec: ExecutionContext): Future[immutable.Seq[ItemInfo]] = {

    import format._

    val parameters = Seq(
      "pio_appkey" -> apiKey,
      "pio_iid" -> targetId.value,
      "pio_n" -> s"$n"
    ) ++
      (if (types.nonEmpty) Seq("pio_itypes" -> types.mkString(",")) else Nil) ++
      (if (attributes.nonEmpty) Seq("pio_attributes" -> attributes.mkString(",")) else Nil) ++
      location.map(x => Seq("pio_latlng" -> s"${x.latitude},${x.longitude}")).getOrElse(Nil) ++
      distance.map {
        case Km(value) => Seq("pio_within" -> s"$value", "pio_unit" -> "km")
        case Mi(value) => Seq("pio_within" -> s"$value", "pio_unit" -> "mi")
      }.getOrElse(Nil)

    lazy val queryStr= s" id($targetId) in engine($engine). Searching with types [${types.mkString(",")}], attributes [${attributes.mkString(",")}}], location ($location) and distance ($distance)"
    WS.url(s"$endpoint/engines/itemsim/$engine/topn.json").withQueryString(parameters: _*).get().flatMap {
      case response if response.status == 404 && isNoRecommendationMessage(response.json)=> Future.failed(new NoRecommendationException(response.statusText+ queryStr))
      case response if response.status >= 500 => Future.failed(new PredictionIOServerException(response.body))
      case response if response.status >= 300 => Future.failed(new PredictionIOClientException(response.body))
      case response => Json.fromJson[immutable.Seq[ItemInfo]](response.json).fold(invalid => Future.failed(new PredictionIOClientException(response.body)), Future.successful)
    }
  }

  def isNoRecommendationMessage(json:JsValue):Boolean={
    (json\"message").validate[String].fold(
    errors => false,
    message => message.contains("Cannot find recommendation for user.")
    )
  }

}