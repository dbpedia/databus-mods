package org.dbpedia.databus.mods.worker.dummy

import org.apache.commons.io.IOUtils
import org.dbpedia.databus.mods.core.model.ModActivity
import org.dbpedia.databus.mods.core.worker.api.ModActivityClientHttp
import org.dbpedia.databus.mods.worker.springboot.controller.{ActivityController, ActivityControllerPollImpl}
import org.dbpedia.databus.mods.worker.springboot.service.ActivityExecutionService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, MediaType, ResponseEntity}
import org.springframework.util.LinkedMultiValueMap

import java.net.URI


@SpringBootTest(
  webEnvironment = WebEnvironment.RANDOM_PORT,
  classes = Array(classOf[DummyWorker])
)
class DummyTest {

  @LocalServerPort
  private var port: Integer = _

  @Autowired
  private var activityService: ActivityExecutionService = _

  @Autowired
  private var workerApi: ActivityController = _

  @Autowired
  private var databusImpl: FakeDatabusImpl = _

  @Autowired
  private var modActivity: ModActivity = _

  @Test
  def contextLoad(): Unit = {
    assert(activityService != null)
    assert(workerApi != null)
    assert(workerApi.isInstanceOf[ActivityControllerPollImpl])
    assert(modActivity != null);
    assert(databusImpl != null)
  }

  @Test
  def test(): Unit = {

    val client = new ModActivityClientHttp

    val result = client.send(
      new URI(s"http://localhost:${port}/activity"),
      new URI(s"http://localhost:${port}/publisher/group/artifact/version/file"),
      minDelay = 1000
    )

    IOUtils.write(result.data,System.out)
  }
}
