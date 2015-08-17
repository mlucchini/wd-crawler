package crawling

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import dispatch.Defaults._
import dispatch._
import fetching.NioFetcher
import parsing.LinkExtractor

import scala.collection.JavaConverters.asScalaSetConverter
import scala.concurrent.Await
import scala.concurrent.duration._

class ParallelCrawler(domain: String, fetcher: NioFetcher, timeout: Duration) extends Crawler {
  private val visiting = Collections.newSetFromMap(new ConcurrentHashMap[String, java.lang.Boolean]).asScala

  override def siteMap() = Await.result(visit(domain), timeout).toSeq.sorted

  private def visit(link: String): Future[Set[String]] = {
    if (!link.startsWith(domain) || visiting.contains(link)) Future.successful(Set(link))
    else {
      visiting += link
      fetcher.get(link).map {
        case None => Set.empty
        case Some(content) => LinkExtractor(domain).fromString(content)
      }.flatMap {
        links => Future.fold(links.map(visit))(Set(link))(_ ++ _)
      }
    }
  }
}