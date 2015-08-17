package parsing

import org.scalatest.FunSpec

class LinkExtractorTest extends FunSpec {
  def fixture = new {
    val domain = "http://mydomain.com"
    val externalAbsoluteUrl = "http://google.fr/search"
    val externalAbsoluteUrlWithProtocolRelative = "//google.fr/search"
    val internalAbsoluteUrl = "http://mydomain.com/blog"
    val internalAbsoluteUrlWithProtocolRelative = "//mydomain.com/blog"
    val internalRelativeUrl = "/blog"
  }

  describe("A link extractor") {
    it("should fail gracefully and return an empty set if the input content is not html") {
      val f = fixture

      val html = "Hello world!"

      assertResult(Set())(LinkExtractor(f.domain).fromString(html))
    }

    it("should find internal and external absolute URIs") {
      val f = fixture

      val html = s"<html><body><div>" +
        s"<a href='${f.internalAbsoluteUrl}'>Link</a>" +
        s"<a href='${f.externalAbsoluteUrl}'>Link</a>" +
        s"</div></body></html>"

      assertResult(Set(f.internalAbsoluteUrl, f.externalAbsoluteUrl))(LinkExtractor(fixture.domain).fromString(html))
    }

    it("should find internal relative URIs and return them as absolute URIs") {
      val f = fixture

      val html = s"<html><body><div><a href='${f.internalRelativeUrl}'>Link</a></div></body></html>"

      assertResult(Set(f.internalAbsoluteUrl))(LinkExtractor(fixture.domain).fromString(html))
    }

    it("should find protocol-relative URIs and return them as absolute URIs") {
      val f = fixture

      val html = s"<html><body><div>" +
        s"<a href='${f.internalAbsoluteUrlWithProtocolRelative}'>Link</a>" +
        s"<a href='${f.externalAbsoluteUrlWithProtocolRelative}'>Link</a>" +
        s"</div></body></html>"

      assertResult(Set(f.internalAbsoluteUrl, f.externalAbsoluteUrl))(LinkExtractor(fixture.domain).fromString(html).toSet)
    }

    it("should ignore anchors") {
      val f = fixture

      val html = s"<html><body><div><a href='${f.internalAbsoluteUrl}#anchor'>Link</a></div></body></html>"

      assertResult(Set(f.internalAbsoluteUrl))(LinkExtractor(fixture.domain).fromString(html))
    }

    it("should ignore duplicates") {
      val f = fixture

      val html = s"<html><body><div>" +
        s"<a href='${f.internalRelativeUrl}'>Link1</a>" +
        s"<a href='${f.internalAbsoluteUrl}'>Link2</a>" +
        s"<a href='${f.internalAbsoluteUrl}#anchor'>Link3</a>" +
        s"</div></body></html>"

      assertResult(Set(f.internalAbsoluteUrl))(LinkExtractor(fixture.domain).fromString(html))
    }

    it("should not contain email addresses") {
      val f = fixture

      val html = "<html><body><a href='mailto:a@b.c'>My email</a></html></body>"

      assertResult(Set())(LinkExtractor(f.domain).fromString(html))
    }
  }
}