package com.funtis.commons.http

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Sławomir Kluz on 04/10/2017.
  */
class RestClientRequestTest extends FlatSpec with Matchers with LazyLogging {

  behavior of "base uri"

  it should "handle url without path" in {
    new RestClient.Builder().baseUri("https://10.0.0.1").buildRequest(Method.GET).url shouldBe "https://10.0.0.1/"
    new RestClient.Builder().baseUri("http://localhost/").buildRequest(Method.GET).url shouldBe "http://localhost/"
    new RestClient.Builder().baseUri("https://10.0.0.1/api").buildRequest(Method.GET).url shouldBe "https://10.0.0.1/api"
    new RestClient.Builder().baseUri("http://localhost/api/").buildRequest(Method.GET).url shouldBe "http://localhost/api/"
  }

  Seq(
    ("http://foo.com/blah_blah?", "http://foo.com/blah_blah?"),
    ("http://foo.com/blah_blah/?", "http://foo.com/blah_blah/?"),
    ("http://foo.com/blah_blah/?a", "http://foo.com/blah_blah/?a"),
    ("http://foo.com/blah_blah", "http://foo.com/blah_blah"),
    ("http://foo.com/blah_blah/", "http://foo.com/blah_blah/"),
    ("http://foo.com/blah_blah_(wikipedia)", "http://foo.com/blah_blah_(wikipedia)"),
    ("http://foo.com/blah_blah_(wikipedia)_(again)", "http://foo.com/blah_blah_(wikipedia)_(again)"),
    ("http://www.example.com/wpstyle/?p=364", "http://www.example.com/wpstyle/?p=364"),
    ("https://www.example.com/foo/?bar=baz&inga=42&quux", "https://www.example.com/foo/?bar=baz&inga=42&quux"),
    ("http://✪df.ws/123", "http://✪df.ws/123"),
    ("http://userid:password@example.com:8080", "http://userid:password@example.com:8080/"),
    ("http://userid:password@example.com:8080/", "http://userid:password@example.com:8080/"),
    ("http://userid@example.com", "http://userid@example.com/"),
    ("http://userid@example.com/", "http://userid@example.com/"),
    ("http://userid@example.com:8080", "http://userid@example.com:8080/"),
    ("http://userid@example.com:8080/", "http://userid@example.com:8080/"),
    ("http://userid:password@example.com", "http://userid:password@example.com/"),
    ("http://userid:password@example.com/", "http://userid:password@example.com/"),
    ("http://➡.ws/䨹", "http://➡.ws/䨹"),
    ("http://⌘.ws", "http://⌘.ws/"),
    ("http://⌘.ws/", "http://⌘.ws/"),
    ("http://foo.com/blah_(wikipedia)#cite-1", "http://foo.com/blah_(wikipedia)#cite-1"),
    ("http://foo.com/blah_(wikipedia)_blah#cite-1", "http://foo.com/blah_(wikipedia)_blah#cite-1"),
    ("http://foo.com/unicode_(✪)_in_parens", "http://foo.com/unicode_(✪)_in_parens"),
    ("http://foo.com/(something)?after=parens", "http://foo.com/(something)?after=parens"),
    ("http://☺.damowmow.com/", "http://☺.damowmow.com/"),
    ("http://code.google.com/events/#&product=browser", "http://code.google.com/events/#&product=browser"),
    ("http://j.mp", "http://j.mp/"),
    ("ftp://foo.bar/baz", "ftp://foo.bar/baz"),
    //("http://foo.bar/?q=Test%20URL-encoded%20stuff","http://foo.bar/?q=Test%20URL-encoded%20stuff"),
    ("http://مثال.إختبار", "http://مثال.إختبار/"),
    ("http://例子.测试", "http://例子.测试/"),
    //("http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com","http://-.~_!$&'()*+,;=:%40:80%2f::::::@example.com/"),
    ("http://1337.net", "http://1337.net/"),
    ("http://a.b-c.de", "http://a.b-c.de/"),
    ("http://223.255.255.254", "http://223.255.255.254/"),
    ("abc://username:password@example.com:123/path/data?key=value&key2=value2#fragid1", "abc://username:password@example.com:123/path/data?key=value&key2=value2#fragid1")
  ).foreach { case (baseUri, expected) =>
    it should s"handle correct [$baseUri] url" in {
      new RestClient.Builder().baseUri(baseUri).buildRequest(Method.GET).url shouldBe expected
    }
  }

  Seq(
    "http://",
    "http://foo.bar?q=Spaces should be encoded",
    "//",
    "http:// shouldfail.com",
    ":// should fail",
    "http://www.api.com/{version}",
    "http://foo.bar/foo(bar)baz quux",
  ).foreach(baseUri => {
    it should s"fail bad [$baseUri] url" in {
      an[RequestValidationException] should be thrownBy new RestClient.Builder().baseUri(baseUri).buildRequest(Method.GET)
    }
  })

  behavior of "path"

  Seq(
    ("http://onet.pl", null, "http://onet.pl/"),
    ("http://onet.pl", "", "http://onet.pl/"),
    ("http://onet.pl", "/", "http://onet.pl/"),
    ("http://onet.pl", "resource", "http://onet.pl/resource"),
    ("http://onet.pl", "/resource", "http://onet.pl/resource"),
    ("http://onet.pl", "/resource/", "http://onet.pl/resource/"),
    ("http://onet.pl", "/resource/v1", "http://onet.pl/resource/v1"),
    ("http://onet.pl/", null, "http://onet.pl/"),
    ("http://onet.pl/", "", "http://onet.pl/"),
    ("http://onet.pl/", "/", "http://onet.pl/"),
    ("http://onet.pl/", "resource", "http://onet.pl/resource"),
    ("http://onet.pl/", "/resource", "http://onet.pl/resource"),
    ("http://onet.pl/", "/resource/", "http://onet.pl/resource/"),
    ("http://onet.pl/", "/resource/v1", "http://onet.pl/resource/v1"),
    ("http://onet.pl/api", null, "http://onet.pl/api"),
    ("http://onet.pl/api", "", "http://onet.pl/api"),
    ("http://onet.pl/api", "/", "http://onet.pl/api/"),
    ("http://onet.pl/api", "resource", "http://onet.pl/api/resource"),
    ("http://onet.pl/api", "/resource", "http://onet.pl/api/resource"),
    ("http://onet.pl/api", "/resource/", "http://onet.pl/api/resource/"),
    ("http://onet.pl/api", "/resource/v1", "http://onet.pl/api/resource/v1"),
    ("http://onet.pl/api/", null, "http://onet.pl/api/"),
    ("http://onet.pl/api/", "", "http://onet.pl/api/"),
    ("http://onet.pl/api/", "/", "http://onet.pl/api/"),
    ("http://onet.pl/api/", "resource", "http://onet.pl/api/resource"),
    ("http://onet.pl/api/", "/resource", "http://onet.pl/api/resource"),
    ("http://onet.pl/api/", "/resource/", "http://onet.pl/api/resource/"),
    ("http://onet.pl/api/", "/resource/v1", "http://onet.pl/api/resource/v1"),
    ("http://onet.pl/api/v2", null, "http://onet.pl/api/v2"),
    ("http://onet.pl/api/v2", "", "http://onet.pl/api/v2"),
    ("http://onet.pl/api/v2", "/", "http://onet.pl/api/v2/"),
    ("http://onet.pl/api/v2", "resource", "http://onet.pl/api/v2/resource"),
    ("http://onet.pl/api/v2", "/resource", "http://onet.pl/api/v2/resource"),
    ("http://onet.pl/api/v2", "/resource/", "http://onet.pl/api/v2/resource/"),
    ("http://onet.pl/api/v2", "/resource/v1", "http://onet.pl/api/v2/resource/v1"),
    ("http://onet.pl/api/v2/", null, "http://onet.pl/api/v2/"),
    ("http://onet.pl/api/v2/", "", "http://onet.pl/api/v2/"),
    ("http://onet.pl/api/v2/", "/", "http://onet.pl/api/v2/"),
    ("http://onet.pl/api/v2/", "resource", "http://onet.pl/api/v2/resource"),
    ("http://onet.pl/api/v2/", "/resource", "http://onet.pl/api/v2/resource"),
    ("http://onet.pl/api/v2/", "/resource/", "http://onet.pl/api/v2/resource/"),
    ("http://onet.pl/api/v2/", "/resource/v1", "http://onet.pl/api/v2/resource/v1"),
    ("http://onet.pl/api/v2?query=test", null, "http://onet.pl/api/v2?query=test"),
    ("http://onet.pl/api/v2?query=test", "", "http://onet.pl/api/v2?query=test"),
    ("http://onet.pl/api/v2?query=test", "/", "http://onet.pl/api/v2/?query=test"),
    ("http://onet.pl/api/v2?query=test", "resource", "http://onet.pl/api/v2/resource?query=test"),
    ("http://onet.pl/api/v2?query=test", "/resource", "http://onet.pl/api/v2/resource?query=test"),
    ("http://onet.pl/api/v2?query=test", "/resource/", "http://onet.pl/api/v2/resource/?query=test"),
    ("http://onet.pl/api/v2?query=test", "/resource/v1", "http://onet.pl/api/v2/resource/v1?query=test"),
    ("http://onet.pl/api/v2/?query=test", null, "http://onet.pl/api/v2/?query=test"),
    ("http://onet.pl/api/v2/?query=test", "", "http://onet.pl/api/v2/?query=test"),
    ("http://onet.pl/api/v2/?query=test", "/", "http://onet.pl/api/v2/?query=test"),
    ("http://onet.pl/api/v2/?query=test", "resource", "http://onet.pl/api/v2/resource?query=test"),
    ("http://onet.pl/api/v2/?query=test", "/resource", "http://onet.pl/api/v2/resource?query=test"),
    ("http://onet.pl/api/v2/?query=test", "/resource/", "http://onet.pl/api/v2/resource/?query=test"),
    ("http://onet.pl/api/v2/?query=test", "/resource/v1", "http://onet.pl/api/v2/resource/v1?query=test")
  ).foreach { case (baseUri, path, expected) => {
    it should s"handle [$baseUri] + [$path] => [$expected]" in {
      new RestClient.Builder().baseUri(baseUri).path(path).buildRequest(Method.GET).url shouldBe expected
    }
  }
  }

  behavior of "path param"

  it should "handle simple path params" in {
    new RestClient.Builder().baseUri("https://www.wp.pl/api")
      .pathParam("int", 1).pathParam("string", "big").pathParam("bool", false)
      .path("/resource/{int}/{string}/{bool}").buildRequest(Method.GET).url shouldBe "https://www.wp.pl/api/resource/1/big/false"
    new RestClient.Builder().baseUri("https://www.wp.pl/api")
      .pathParam("a", "A").pathParam("b", "B")
      .path("{a}{b}").buildRequest(Method.GET).url shouldBe "https://www.wp.pl/api/AB"
    new RestClient.Builder().baseUri("https://www.wp.pl/api")
      .pathParam("b", "B").pathParam("a", "A")
      .path("{a}{b}").buildRequest(Method.GET).url shouldBe "https://www.wp.pl/api/AB"
  }

  it should "fail when not all params are provided" in {
    an[RequestValidationException] should be thrownBy new RestClient.Builder().baseUri("https://www.wp.pl/api")
      .pathParam("int", 1)
      .pathParam("string", "big")
      .path("/resource/{int}/{string}/{bool}").buildRequest(Method.GET)
  }

  it should "fail when there are to many params" in {
    an[RequestValidationException] should be thrownBy new RestClient.Builder().baseUri("https://www.wp.pl/api")
      .pathParam("int", 1)
      .pathParam("string", "big")
      .path("/resource/{int}").buildRequest(Method.GET)
  }

  behavior of "query"

  it should "handle single param" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/path").queryParam("sort", "desc").buildRequest(Method.GET).url shouldBe "https://www.google.com/path?sort=desc"
  }

  it should "handle multiple params" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/search").queryParam("sort", "name").queryParam("order", "desc").buildRequest(Method.GET).url shouldBe "https://www.google.com/search?sort=name&order=desc"
  }

  it should "handle empty param" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/search").queryParam("sort", "").buildRequest(Method.GET).url shouldBe "https://www.google.com/search?sort="
  }

  it should "handle some option param" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/search").queryParam("sort", Some("desc")).buildRequest(Method.GET).url shouldBe "https://www.google.com/search?sort=desc"
  }

  it should "handle none option param" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/search").queryParam("sort", None).buildRequest(Method.GET).url shouldBe "https://www.google.com/search"
  }

  it should "handle list of param" in {
    new RestClient.Builder().baseUri("https://www.google.com").path("/search").queryParam(Seq("a" -> "b", "c" -> "d")).buildRequest(Method.GET).url shouldBe "https://www.google.com/search?a=b&c=d"
  }


  ////  behavior of "body"
  ////
  ////  it should "handle empty body" in {
  ////    assert(new RestClient.Builder().baseUri("https://www.google.com").request(Method.POST).body == null)
  ////  }
  ////
  ////  it should "handle string type body" in {
  ////    new RestClient.Builder().baseUri("https://www.google.com").body("dupa").request(Method.POST).body shouldBe "dupa"
  ////  }
  ////
  ////  it should "handle primitive type body" in {
  ////    new RestClient.Builder().baseUri("https://www.google.com").body(1).request(Method.POST).body shouldBe 1
  ////  }
  ////
  ////  it should "handle case class body" in {
  ////    new RestClient.Builder().baseUri("https://www.google.com").body(SimpleCase(10)).request(Method.POST).body shouldBe SimpleCase(10)
  ////  }

}
