package org.dbpedia.databus_mods.server.core.demo

import java.util.function.Consumer

import javax.servlet.http.HttpServletResponse
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, RequestMapping, RestController,RequestParam}

@RestController
@RequestMapping(value = Array("demo"))
class ModApi(
              modProcessor: ModProcessor,
              modRepo: ModRepo) {

  @GetMapping(value = Array("{publisher}/{group}/{artifact}/{version}/{variant}"))
  def default(
               @PathVariable publisher: String,
               @PathVariable group: String,
               @PathVariable artifact: String,
               @PathVariable version: String,
               @PathVariable variant: String,
               @RequestParam(required = false) source: String,
               response: HttpServletResponse)
  : Unit = {

    val simpleId = s"$publisher/$group/$artifact/$version/$variant"

    //TODO add fileUri parma and validate

    modRepo.cacheMap.getOrDefault(simpleId,null) match {
      case Some(model) =>
        response.setStatus(200)
        RDFDataMgr.write(response.getOutputStream, model, Lang.TURTLE)
      case None =>
        response.setStatus(500)
      case _ =>
        modProcessor.addProcess(simpleId)
        response.setStatus(202)
    }
  }
}
