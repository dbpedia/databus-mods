package org.dbpedia.databus.mods.core.worker.api

import org.apache.commons.io.IOUtils
import org.apache.jena.riot.{Lang, RDFWriter, RIOT}
import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.worker.exec.ActivityExecution
import org.dbpedia.databus.mods.core.model.ModActivityRequest

import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
 * An Implementation of the Worker Mod Activity HTTP API
 */
class ModActivityApiHttpPoll(activityExecution: ActivityExecution) {

  val dataIdByJob = new ConcurrentHashMap[Long,String]()
  val currentJobId = new AtomicLong(0)

  def handleRequest(request: HttpServletRequest, response: HttpServletResponse): Unit = {

    request.getMethod match {
      case "POST" =>
        handlePOST(request, response)
      case "GET" =>
        handleGET(request, response)
    }
  }

  private def handlePOST(request: HttpServletRequest, response: HttpServletResponse): Unit = {

    createModActivityRequest(request) match {
      case Some(modActivityRequest: ModActivityRequest) =>
        response.setStatus(202)
        activityExecution.submit(modActivityRequest)
        val jobId = currentJobId.getAndIncrement()
        dataIdByJob.put(jobId,modActivityRequest.dataId)
        response.setHeader("location",s"/${jobId.toString}/activity")
      case None =>
        response.setStatus(400)
    }
  }

  private def handleGET(request: HttpServletRequest, response: HttpServletResponse): Unit = {

    val jobId = request.getRequestURI.split("/").reverse(1)

    val dataId = dataIdByJob.get(jobId.toLong)
    if(null == dataId) {
      response.setStatus(404)
    } else {
          activityExecution.get(dataId) match {
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
                  .set(RIOT.symTurtleOmitBase, "false")
                  .lang(Lang.TRIG)
                  .base("http://example.org/space/")
                  .source(mam.createRdfModel(new URI("http://example.org/space/")))
                  .output(os);
                os.close()
              } else {
                response.setStatus(202)
                response.setHeader("location", request.getRequestURI)
                response.setHeader("retry-after", 0.toString)
              }
          }
    }
  }

  def createModActivityRequest(request: HttpServletRequest): Option[ModActivityRequest] = {
    val dataId = request.getParameter("dataId")
    val accessUri = request.getParameter("accessUri")
    Some(ModActivityRequest(dataId, Some(accessUri)))
  }
}
