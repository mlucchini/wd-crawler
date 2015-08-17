package crawling

trait Crawler {
  def siteMap(): Iterable[String]
}