package org.dbpedia.databus.mods.worker.springboot.controller

import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.dbpedia.databus.mods.core.worker.api.ModActivityApiHttpPoll
import org.dbpedia.databus.mods.worker.springboot.service.ActivityExecutionService

class ActivityControllerPollImpl(activityExecutionService: ActivityExecutionService) extends ActivityController {

  private val api = new ModActivityApiHttpPoll(activityExecutionService)

  override def handleRequest(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): Unit = {
    api.handleRequest(request,response)
  }
}
