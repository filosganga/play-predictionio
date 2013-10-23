package io.prediction.play

import play.api.Application
import io.prediction.Client
import org.mockito.Mockito._
import org.mockito.Matchers._

/**
 *
 * @author Filippo De Luca - me@filippodeluca.com
 */
class MockedPredictionIoPlugin(app: Application) extends PredictionIoPlugin(app) {

  val mockedClient = mock(classOf[Client])
  when(mockedClient.getCreateUserRequestBuilder(anyString())).thenCallRealMethod()
  when(mockedClient.getCreateItemRequestBuilder(anyString(), any())).thenCallRealMethod()

  override protected def createClient(appKey: String, uri: String, threadLimit: Int): Client = mockedClient

  override protected def closeClient(client: Client){
    // Do nothing
  }
}
