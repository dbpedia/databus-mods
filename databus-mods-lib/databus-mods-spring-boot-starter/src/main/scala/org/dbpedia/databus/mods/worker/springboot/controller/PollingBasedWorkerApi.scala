package org.dbpedia.databus.mods.worker.springboot.controller

import org.apache.commons.io.IOUtils
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFWriter, RIOT}
import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.model.{ModActivity, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.service.ActivityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import java.nio.charset.StandardCharsets
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class PollingBasedWorkerApi(activityService: ActivityService) extends WorkerApi {

  private val log = LoggerFactory.getLogger(classOf[PollingBasedWorkerApi])

  override def handleRequest(didPart: Part, request: HttpServletRequest, response: HttpServletResponse): Unit = {

    request.getMethod match {
      case "POST" =>
        handlePOST(didPart, request, response)
      case "GET" =>
        handleGET(didPart, request, response)
    }
  }

  private def handlePOST(didPart: Part, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val os = response.getOutputStream

    val source = request.getParameter("source")
    if (null == source) {
      response.setStatus(500)
      IOUtils.write("param <source> missing", os, StandardCharsets.UTF_8)
    } else {
      didPart.downloadURL = source
      val activityRequest = ModActivityRequest(didPart.uri,didPart.downloadURL)
      activityService.submit(activityRequest)
      response.setStatus(202)
      response.setHeader("location", request.getRequestURI)
    }
    os.close()
  }

  private def handleGET(didPart: Part, request: HttpServletRequest, response: HttpServletResponse): Unit = {

    activityService.get(didPart.uri) match {
      case None =>
        response.setStatus(404)
      case Some(fMam) =>
        if (fMam.isDone) {
          // can throw an exception which is caught in WorkerApi abstract
          val mam = fMam.get()
          response.setStatus(200)
          val os = response.getOutputStream
          RDFWriter.create()
            .set(RIOT.symTurtleDirectiveStyle, "n3")
            .set(RIOT.symTurtleOmitBase,"false")
            .lang(Lang.TRIG)
            .base("http://example.org/space/")
            .source(mam.createRdfModel("http://example.org/space/"))
            .output(os);
          os.close()
        } else {
          response.setStatus(202)
          response.setHeader("location", request.getRequestURI)
        }
    }
  }

}
