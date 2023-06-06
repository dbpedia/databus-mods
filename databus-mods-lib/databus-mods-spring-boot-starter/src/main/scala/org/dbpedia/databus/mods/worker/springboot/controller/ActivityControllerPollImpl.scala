package org.dbpedia.databus.mods.worker.springboot.controller

import org.apache.commons.io.IOUtils
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFWriter, RIOT}
import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityRequest}
import org.dbpedia.databus.mods.core.worker.api.ModActivityApiHttpPoll
import org.dbpedia.databus.mods.worker.springboot.service.ActivityExecutionService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod}

import java.nio.charset.StandardCharsets
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class ActivityControllerPollImpl(activityExecutionService: ActivityExecutionService) extends ActivityController {

  private val api = new ModActivityApiHttpPoll(activityExecutionService)

  override def handleRequest(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): Unit = {
    api.handleRequest(request,response)
  }
}
