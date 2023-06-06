package org.dbpedia.databus.mods.core.worker.api

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.{NameValuePair, StatusLine}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.jena.sparql.function.library.leviathan.log
import org.dbpedia.databus.mods.core.model.ModActivityResponse
import org.slf4j.LoggerFactory

import java.io.ByteArrayOutputStream
import java.net.{URI, URL}
import java.nio.charset.StandardCharsets
import java.util
import scala.util.Try

class ModActivityClientHttp {

  private val log = LoggerFactory.getLogger(classOf[ModActivityClientHttp])

  def send(endpoint: URI, dataId: URI, accessUri: Option[URI] = None, minDelay: Int = 0): ModActivityResponse = {

    var follow: Option[URI] = None
    val bos = new ByteArrayOutputStream()

    val httpClient = HttpClientBuilder.create().disableRedirectHandling().build()
    val httpPost = new HttpPost(endpoint)
    val postParameters = new util.ArrayList[NameValuePair]()
    postParameters.add(new BasicNameValuePair("dataId", dataId.toString))
    httpPost.setEntity(new UrlEncodedFormEntity(postParameters, StandardCharsets.UTF_8.name()))

    httpClient.execute(httpPost) match {
      case okResponse if okResponse.getStatusLine.getStatusCode == 200 =>
        log.debug(s"status ${okResponse.getStatusLine}")
        okResponse.getEntity.writeTo(bos)
      case followableResponse if needsFollow(followableResponse.getStatusLine) =>
        log.debug(s"status ${followableResponse.getStatusLine}")
        val location = getLocationHeader(followableResponse)
        follow =
          if (location.isEmpty) None // TODO Exception
          else Some(new URL(endpoint.toURL,location.get).toURI)
      case errorResponse =>
        log.debug(s"status ${errorResponse.getStatusLine}")
        throw new Exception(errorResponse.getStatusLine.toString)
    }

    while (follow.isDefined) {
      Thread.sleep(minDelay)
      val getResponse = httpClient.execute(new HttpGet(follow.get))
      getResponse match {
        case okResponse if okResponse.getStatusLine.getStatusCode == 200 =>
          log.debug(s"status ${okResponse.getStatusLine}")
          okResponse.getEntity.writeTo(bos)
          follow = None
        case followRequest if needsFollow(followRequest.getStatusLine) =>
          log.debug(s"status ${followRequest.getStatusLine}")
          val location = getLocationHeader(getResponse)
          follow =
            if (location.isEmpty) None // TODO Exception
            else Some(new URL(endpoint.toURL,location.get).toURI)
        case errorResponse =>
          log.debug(s"status ${errorResponse.getStatusLine}")
          throw new Exception(errorResponse.getStatusLine.toString)
      }
    }
    ModActivityResponse(dataId.toString, bos.toByteArray)
  }

  private def getLocationHeader(response: CloseableHttpResponse): Option[String] = Try {
    val locationValues = response.getFirstHeader("Location").getElements
    locationValues(0).toString
  }.toOption

  private def getRetryAfterHeader(response: CloseableHttpResponse): Option[String] = Try {
    val retryAfterValues = response.getFirstHeader("Retry-After").getElements
    retryAfterValues(0).toString
  }.toOption

  private def needsFollow(line: StatusLine): Boolean = {
    val code = line.getStatusCode
    code match {
      case accepted if accepted == 202 => true
      case redirect if redirect >= 300 && redirect < 400 => true
      case _ => false
    }
  }
}
