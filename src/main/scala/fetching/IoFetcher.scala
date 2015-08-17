package fetching

import scala.io.Source
import scala.util.Try

class IoFetcher {
  def get(uri: String): Option[String] = {
    Try {
      val source = Source.fromURL(uri)
      val content = source.mkString
      source.close()
      content
    }.toOption
  }
}