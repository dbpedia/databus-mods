import org.springframework.http.{HttpEntity, HttpMethod}
import org.springframework.web.client.RestTemplate

val requestEntity = new HttpEntity[String]("")
val responseEntity = new RestTemplate().exchange("http://ts.v122.de",HttpMethod.GET, requestEntity, classOf[String])

println(responseEntity.getBody)