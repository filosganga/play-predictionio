package com.github.filosganga.play.predictionio

import org.specs2.mock.Mockito
import play.api.Plugin
import com.github.filosganga.play.predictionio.{HasApi, Api}

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class MockedHasApi(app: play.api.Application) extends Plugin with HasApi with Mockito {

  val api: Api = mock[Api]

}
