package org.dbpedia.databus.mods.worker.dummy

import org.dbpedia.databus.mods.model.ModActivity
import org.dbpedia.databus.mods.worker.springboot.controller.{PollingBasedWorkerApi, WorkerApi}
import org.dbpedia.databus.mods.worker.springboot.service.ActivityService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, MediaType, ResponseEntity}
import org.springframework.util.LinkedMultiValueMap


@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  classes = Array(classOf[DummyDatabusModWorker])
)
class DummyTest {

  @LocalServerPort
  private var port: Integer = _

  @Autowired
  private var restTemplate: TestRestTemplate = _

  @Autowired
  private var activityService: ActivityService = _

  @Autowired
  private var workerApi: WorkerApi = _

  @Autowired
  private var modActivity: ModActivity = _

  @Test
  def contextLoad(): Unit = {
    assert(activityService != null)
    assert(workerApi != null)
    assert(workerApi.isInstanceOf[PollingBasedWorkerApi])
    assert(modActivity != null);
  }

  @Test
  def test(): Unit = {
    val path = "/vehnem/paper-supplements/demo-graph/20210301/demo-graph.nt.gz";

    val headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    val map= new LinkedMultiValueMap[String,String]();
    map.add("source", "file:///proc/cpuinfo");

    var lastStatusCode = 0;

    val rePOST = this.restTemplate.exchange(
      "http://localhost:"+port+path+"/activity",
      HttpMethod.POST,
      new HttpEntity(map,headers),
      classOf[String])

    lastStatusCode = rePOST.getStatusCodeValue
    assert(lastStatusCode == 202,"wrong status code response POST")

    var body = "";
    while (lastStatusCode == 202) {
      val reGET = this.restTemplate.getForEntity(
        "http://localhost:"+port+path+"/activity",
        classOf[String])
      lastStatusCode = reGET.getStatusCodeValue
      body = reGET.getBody;
    }

    System.out.println(body);
    assert(lastStatusCode == 200);
  }
}
