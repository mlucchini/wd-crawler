package crawling

import fetching.IoFetcher
import parsing.LinkExtractor

class SerialCrawler(domain: String, fetcher: IoFetcher) extends Crawler {
  override def siteMap() = {
    var seen = Set(domain)
    var visited = Set(domain)
    var toVisit = Set(domain)

    while (toVisit.nonEmpty) {
      val link = toVisit.head
      toVisit -= link
      visited += link
      fetcher.get(link) match {
        case None =>
        case Some(html) =>
          val links = LinkExtractor(domain).fromString(html)
          toVisit ++= links.filter(_.startsWith(domain)).filter(!visited.contains(_))
          seen ++= links
      }
    }

    seen.toSeq.sorted
  }
}