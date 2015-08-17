package fetching

import dispatch.Defaults._
import dispatch._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object NioFetcher {
  private val client = Http.configure(_ setFollowRedirects true)
}

class NioFetcher {
  def get(uri: String): Future[Option[String]] = {
    Try { url(new Uri(uri).toString) } match {
      case Success(source) => NioFetcher.client(source OK as.String).option
      case Failure(t) => Future.successful(None)
    }
  }
}