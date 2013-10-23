package io.prediction.play

import play.api.{Logger, Application}
import scala.concurrent._
import io.prediction.{Client, User}
import scala.Some

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
object PredictionIO {

  private val logger = Logger(getClass)

  private def plugin(implicit app: Application): PredictionIoPlugin = {

    app.plugin[PredictionIoPlugin] match {
      case Some(plugin) => plugin
      case None => throw new Exception("There is no cache plugin registered. Make sure at least one PredictionIoPlugin implementation is enabled.")
    }

  }

  def createUser(uid: String, location: Option[Location] = None)(implicit app: Application): Promise[User] =
    plugin.promise(userFor(uid, location))

  private def userFor(uid: String, location: Option[Location])(client: Client): User = {
    client.createUser(location.foldLeft(client.getCreateUserRequestBuilder(uid))((s,x) =>
      s.latitude(x.latitude).longitude(x.longitude)
    ))

    val created = location.foldLeft(new User(uid))((s, x)=> s.latitude(x.latitude).longitude(x.longitude))

    logger.debug("Created user with uid=" + created.getUid)

    created
  }

}
