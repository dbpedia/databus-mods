package lib.util

import java.io.ByteArrayOutputStream
import java.net.{URI, URL}

import org.apache.http.StatusLine
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.jena.rdf.model.{Model, ModelFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.slf4j.LoggerFactory

class ModApiUtil
object ModApiUtil {

  private val log = LoggerFactory.getLogger(classOf[ModApiUtil])

  private val defaultRetrySeconds = 200
  private val maxRetrySeconds = 60 * 60 * 1000

  //  def get(): Unit = {
  //    val response: Response = Request.Get("").execute()
  //  }
  //
  //  def post(): Unit = {
  //
  //  }
  //
  //  def delete(): Unit = {
  //
  //
  //  }

  def submitAndPoll(uri: URI, defaultRetryDelay: Int = 0): (Array[Byte],URI) = {
    var follow: Option[URI] = None
    val bos = new ByteArrayOutputStream()
    var baseURI = uri
    var retryDelay = defaultRetryDelay

    val httpClient = HttpClientBuilder.create().disableRedirectHandling().build()
    val postResponse = httpClient.execute(new HttpPost(uri))
    postResponse match {
      case ok if ok.getStatusLine.getStatusCode == 200 =>
        ok.getEntity.writeTo(bos)
      case followRequest if needsFollow(followRequest.getStatusLine) =>
        val location = getLocationHeader(postResponse)
        follow = if(location.isEmpty) None else Some(new URI(uri.getScheme+"://"+uri.getHost+":"+uri.getPort+location.get))
      case errorResponse => throw new Exception(errorResponse.getStatusLine.toString)
    }

    while (follow.isDefined) {
      baseURI = follow.get
      Thread.sleep(retryDelay)
      val getResponse = httpClient.execute(new HttpGet(follow.get))
      getResponse match {
        case ok if ok.getStatusLine.getStatusCode == 200 =>
          ok.getEntity.writeTo(bos)
          follow = None
        case followRequest if needsFollow(followRequest.getStatusLine) =>
          val location = getLocationHeader(getResponse)
          follow = if(location.isEmpty) None else Some(new URI(uri.getScheme+"://"+uri.getHost+":"+uri.getPort+location.get))
        case _ => throw new Exception("")
      }
    }
    (bos.toByteArray,baseURI)
  }

  private def getLocationHeader(response: CloseableHttpResponse): Option[String] = {
    val a = response.getFirstHeader("Location").getElements
    Some(a(0).toString)
  }

  private def needsFollow(line: StatusLine): Boolean = {
    val code = line.getStatusCode
    code match {
      case accepted if accepted == 202 => true
      case redirect if redirect >= 300 && redirect < 400 => true
      case _ => false
    }
  }
}
