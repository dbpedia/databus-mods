package org.dbpedia.databus.mods.validate.worker

import org.apache.http.impl.client.{HttpClientBuilder, LaxRedirectStrategy}
import org.springframework.http.client.{ClientHttpRequestFactory, HttpComponentsClientHttpRequestFactory}
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, MediaType, RequestEntity, ResponseEntity}
import org.springframework.util.{LinkedMultiValueMap, MultiValueMap}
import org.springframework.web.client.RestTemplate
import picocli.CommandLine.{Command, Option, Parameters}

import java.io.File
import java.util.concurrent.Callable
import scala.util.{Failure, Success, Try}

@Command(name = "worker", mixinStandardHelpOptions = true)
class ValidateWorker extends Callable[Int] {

  @Option(names = Array("-e", "--endpoint"), required = true)
  var endpoint: String = _

  @Option(names = Array("-i", "--input"), required = true)
  var use: File = _

  @Option(names = Array("--test"), required = false)
  var test: java.net.InetAddress = _

  val factory: ClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
    HttpClientBuilder.create().
      setRedirectStrategy(new LaxRedirectStrategy)
      .build()
  )
  val req = new RestTemplate(factory)

  override def call(): Int = {

    Try {
      val path: String = use.getAbsolutePath.split("/").takeRight(5).mkString("/")

      val headers: HttpHeaders = new HttpHeaders
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)

      val map: MultiValueMap[String, String] = new LinkedMultiValueMap[String, String]
      map.add("source", use.getAbsolutePath)

      var lastStatusCode: Int = 0

      val rePOST: ResponseEntity[String] =
        req.exchange(
          endpoint + path + "/activity",
          HttpMethod.POST,
          new HttpEntity[MultiValueMap[String, String]](map, headers),
          classOf[String]
        )

      lastStatusCode = rePOST.getStatusCodeValue
      assert(lastStatusCode == 202)

      var body: String = rePOST.getBody

      while (lastStatusCode == 202) {
        val reGET: ResponseEntity[String] =
          req.getForEntity(
            endpoint + path + "/activity",
            classOf[String]
          )
        lastStatusCode = reGET.getStatusCodeValue
        body = reGET.getBody
      }

      System.out.println(body)
      assert(lastStatusCode == 200)
    } match {
      case Success(value) => 0
      case Failure(exception) =>
        exception.printStackTrace()
        1
    }
  }
}
