package org.dbpedia.databus_mods.integration.mod

import org.dbpedia.databus_mods.server.Server
import org.dbpedia.databus_mods.server.controller.DemoModConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest(
  classes = Array(classOf[Server])
)
@AutoConfigureMockMvc
class DemoModTest(@Autowired mvc: MockMvc) {

  @Autowired
  private var config: DemoModConfig = _

//  @Test
//  def status200(): Unit = {
//
//    println(config.localRepo)
//
//    mvc.perform(post("/demo/publisher/group/artifact/version/file")
//      .contentType(MediaType.APPLICATION_JSON))
//      .andExpect(status().is(202))
//  }

  //TODO 200
  //TODO 400
}
