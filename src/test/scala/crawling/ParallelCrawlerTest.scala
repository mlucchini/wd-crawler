package crawling

import fetching.NioFetcher
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSpec

import scala.concurrent.Future
import scala.concurrent.duration._

class ParallelCrawlerTest extends FunSpec with MockFactory {
  def fixture = new {
    val domain = "http://mydomain.com"
    val externalDomain = "http://yourdomain.com"
    val internalLink = domain + "/blog"
    val externalLink = externalDomain + "/contact"
    val stubFetcher = stub[NioFetcher]
    val crawler = new ParallelCrawler(domain, stubFetcher, 1.seconds)
  }

  describe("A parallel crawler") {
    it("should follow internal links") {
      val f = fixture

      val rootHtml = s"<html><body><div><a href='${f.internalLink}'>Link</a></div></body></html>"
      (f.stubFetcher.get _).when(*).returns(Future.successful(Some(rootHtml)))

      val visited = f.crawler.siteMap()

      assert(visited.contains(f.internalLink))
      (f.stubFetcher.get _).verify(f.internalLink).once()
    }

    it("should not follow external links") {
      val f = fixture

      val rootHtml = s"<html><body><div><a href='${f.externalLink}'>Link</a></div></body></html>"
      (f.stubFetcher.get _).when(*).returns(Future.successful(Some(rootHtml)))

      val visited = f.crawler.siteMap()

      assert(visited.contains(f.externalLink))
      (f.stubFetcher.get _).verify(f.externalLink).never()
    }

    it("should not visit a link twice") {
      val f = fixture

      val rootHtml = s"<html><body><div>" +
        s"<a href='${f.internalLink}'>Link</a>" +
        s"<a href='${f.internalLink}'>Same link</a>" +
        s"</div></body></html>"

      (f.stubFetcher.get _).when(*).returns(Future.successful(Some(rootHtml)))

      f.crawler.siteMap()

      (f.stubFetcher.get _).verify(f.internalLink).once()
    }

    it("should return the entry URI") {
      val f = fixture

      val rootHtml = s"<html><body></body></html>"
      (f.stubFetcher.get _).when(*).returns(Future.successful(Some(rootHtml)))

      val visited = f.crawler.siteMap()

      assertResult(Vector(f.domain))(visited.toVector)
    }

    it("should not return duplicates") {
      val f = fixture

      val internalLink2 = f.domain + "/gallery"
      val internalLink3 = f.domain + "/history"

      val rootHtml = s"<html><body><div><a href='${f.internalLink}'>Link</a><a href='$internalLink2'>Link</a></div></body></html>"
      val internalLinkHtml = s"<html><body><a href='$internalLink3'/></body></html>"
      val internalLink2Html = s"<html><body><a href='$internalLink3'/></body></html>"

      (f.stubFetcher.get _).when(f.internalLink).returns(Future.successful(Some(internalLinkHtml)))
      (f.stubFetcher.get _).when(internalLink2).returns(Future.successful(Some(internalLink2Html)))
      (f.stubFetcher.get _).when(*).returns(Future.successful(Some(rootHtml)))

      val visited = f.crawler.siteMap()

      assertResult(Vector(f.domain, f.internalLink, internalLink2, internalLink3).sorted)(visited.toVector)
    }
  }
}