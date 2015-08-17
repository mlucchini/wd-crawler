import crawling.ParallelCrawler
import fetching.NioFetcher

import scala.concurrent.duration._

object App extends App {
  val defaultDomain = "wiprodigital.com"
  val timeOut = 300.seconds

  val domain = if (args.nonEmpty) args(0)
  else {
    println(s"Usage: crawler [domain]. Defaulting to $defaultDomain")
    defaultDomain
  }

  println(s"Crawling $domain ...")

  val crawler = new ParallelCrawler("http://" + domain, new NioFetcher, timeOut)
  crawler.siteMap().foreach(println)

  sys.exit(0)
}