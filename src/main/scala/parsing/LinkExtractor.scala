package parsing

import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.Jsoup

object LinkExtractor {
  def apply(domain: String) = new LinkExtractor(domain)
}

class LinkExtractor(domain: String) {
  def fromString(html: String) = Jsoup.parse(html, domain).select("a").map(_.attr("abs:href").takeWhile(_ != '#')).filter(!_.startsWith("mailto:")).toSet
}