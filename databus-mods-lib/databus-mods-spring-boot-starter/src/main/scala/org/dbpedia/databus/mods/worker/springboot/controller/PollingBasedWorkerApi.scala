package org.dbpedia.databus.mods.worker.springboot.controller

import java.nio.charset.StandardCharsets

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus.dataid.SingleFile
import org.dbpedia.databus.mods.worker.springboot.service.{ActivityPlan, ModActivity, ActivityService}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

class PollingBasedWorkerApi(activityService: ActivityService) extends WorkerApi {

  @Autowired
  var activityProcessor: ModActivity = _

  private val log = LoggerFactory.getLogger(classOf[PollingBasedWorkerApi])

  override def handleRequest(dbusSF: SingleFile, request: HttpServletRequest, response: HttpServletResponse): Unit = {

    request.getMethod match {
      case "POST" =>
        handlePOST(dbusSF, request, response)
      case "GET" =>
        handleGET(dbusSF, request, response)
    }
  }

  private def handlePOST(dbusSF: SingleFile, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val os = response.getOutputStream

    val source = request.getParameter("source")
    if (null == source) {
      response.setStatus(500)
      IOUtils.write("param <source> missing", os, StandardCharsets.UTF_8)
    } else {
      dbusSF.downloadURL = source
      val activityPlan = new ActivityPlan(dbusSF,activityProcessor)
      activityService.submit(activityPlan)
      response.setStatus(202)
      response.setHeader("location", request.getRequestURI)
    }
    os.close()
  }

  private def handleGET(dbusSF: SingleFile, request: HttpServletRequest, response: HttpServletResponse): Unit = {

    activityService.get(dbusSF) match {
      case None =>
        response.setStatus(404)
      case Some(fMam) =>
        if (fMam.isDone) {
          // can throw an exception which is caught in WorkerApi abstract
          val mam = fMam.get()
          response.setStatus(200)
          val os = response.getOutputStream

          log.info(dbusSF.path + " " + request.getContentType)
          RDFDataMgr.write(os, mam.getModel, Lang.TURTLE)
          os.close()
        } else {
          response.setStatus(202)
          response.setHeader("location", request.getRequestURI)
        }
    }
  }

}
