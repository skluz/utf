package utf.commons.http

import utf.commons.http.Method.Method

/**
  * Created by Sławomir Kluz on 11/09/2017.
  */
case class Specification(
  baseUri: String = "http://localhost/",
  method: Method = Method.GET,
  headers: Headers = new Headers(Header("Content-Type", "application/json; charset=utf-8"), Header("Accept", "application/json"))
)

object Specification {
  lazy val default = Specification()
}