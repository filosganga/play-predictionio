package com.github.filosganga.play.predictionio

import org.specs2.mock.Mockito
import play.api.Plugin


/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class MockedHasApi(app: play.api.Application) extends Plugin with HasApi with Mockito {

  val api: Api = mock[Api]

}
