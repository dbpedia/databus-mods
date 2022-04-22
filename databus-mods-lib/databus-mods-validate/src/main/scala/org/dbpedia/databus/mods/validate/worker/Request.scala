//package org.dbpedia.databus.mods.validate.worker
//
//import org.apache.http.impl.client.{HttpClientBuilder, LaxRedirectStrategy}
//import org.springframework.http.client.{ClientHttpRequestFactory, HttpComponentsClientHttpRequestFactory}
//import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, MediaType}
//import org.springframework.util.LinkedMultiValueMap
//import org.springframework.web.client.RestTemplate
//
//class Request(endpoint: String) extends {
//
//  val factory = new HttpComponentsClientHttpRequestFactory(
//    HttpClientBuilder.create().
//      setRedirectStrategy(new LaxRedirectStrategy)
//      .build()
//  )
//  val req = new RestTemplate(factory)
//
//  def check(databusURI: String): Unit = {
//    val fileID = databusURI.split("/").takeRight(5).mkString("/")
//
//    val map= new LinkedMultiValueMap[String,String]();
//    map.add("source", databusURI);
//
//    val headers = new HttpHeaders
//    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
//
//    val response = req.exchange(
//      endpoint+"/"+fileID+"/activity",
//      HttpMethod.POST,
//      new HttpEntity(map,headers),
//      classOf[String]
//    )
//
//    String =
//
//
////    lastStatusCode = rePOST.getStatusCodeValue();
////    assert(lastStatusCode == 202);
////
////    String body = "";
////    while (lastStatusCode == 202) {
////      ResponseEntity<String> reGET = this.restTemplate.getForEntity(
////        "http://localhost:"+port+path+"/activity",
////        String.class);
////      lastStatusCode = reGET.getStatusCodeValue();
////      body = reGET.getBody();
////    }
//  }
//}
