package org.dbpedia.databus_mods.server.core.service

import java.io.{ByteArrayInputStream, FileOutputStream}
import java.net.URI

import org.apache.commons.io.IOUtils
import org.apache.jena.rdf.model.{ModelFactory, ResourceFactory, SimpleSelector}
import org.apache.jena.riot.system.StreamRDFLib.graph
import org.apache.jena.riot.system.{StreamRDFLib, StreamRDFWriter}
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFFormat}
import org.dbpedia.databus_mods.lib.util.UriUtil
import org.dbpedia.databus_mods.server.core.execution.MetadataExtension
import org.dbpedia.databus_mods.server.core.persistence.Task
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.dbpedia.databus_mods.server.core.io.rdf.RewriteIRIStreamWrapper
import org.springframework.beans.factory.annotation.Value

import scala.collection.JavaConversions._

/**
 * Handles Mod master metadata
 */
@Service
class MetadataService(@Value("${mod-server.web-base}") baseURI: String,
                      taskService: TaskService,
                      vosService: VosService,
                      fileService: FileService) {

  private val log = LoggerFactory.getLogger(classOf[MetadataService])

  def get(task: Task): Unit = {
    // TODO here
  }

  def add(me: MetadataExtension): Unit = {

    val metadataModel = ModelFactory.createDefaultModel()
    val combinedModel = ModelFactory.createDefaultModel()
    // TODO rewrite model

    val modName = me.task.getMod.getName
    val databusPath = new URI(me.task.getDatabusFile.getDataIdSingleFile).getPath


    val oldPlainBase = me.rdfBaseURI.toString.split("\\?").last.replaceAll("[^/]*$","")

    val streamRDF = new RewriteIRIStreamWrapper(
      StreamRDFLib.graph(metadataModel.getGraph),
      regex = s"^${oldPlainBase}/?",
      baseURI.replaceAll("[^/]$", "/") + modName + databusPath + "/")

    val tmpModel = ModelFactory.createDefaultModel()
    val bis = new ByteArrayInputStream(me.rdfByteArray)
    RDFDataMgr.read(tmpModel,bis,me.rdfBaseURI.toString,Lang.TTL)
    bis.close()


    val modResultURIs: List[URI] = {
      tmpModel.query(new SimpleSelector(null, ResourceFactory.createProperty("http://www.w3.org/ns/prov#generated") , null, null))
        .listStatements()
        .toList.map({
        stmt => new URI(stmt.getObject.asResource().getURI)
      }).toList
    }

    tmpModel.getGraph.find().foreach({
      triple => streamRDF.triple(triple)
    })

    val metadataFile = fileService.getOrCreate(modName, databusPath)
    val metadataOS = new FileOutputStream(metadataFile)
    RDFDataMgr.write(metadataOS, metadataModel, RDFFormat.TURTLE_BLOCKS)
    metadataOS.close()

    val selects = me.task.getMod.getSelects.toSet

//    selects.foreach(s => println("selects "+s))

    // TODO base rewrite if RDF?
    // each mod Result should be loaded into a separate graph?
    modResultURIs.foreach({
      modResultURI =>
        val remoteIS = UriUtil.openStream(modResultURI)
        val modResultFileName = modResultURI.toString.split("/").last
        val modResultFile = fileService.getOrCreate(modName, databusPath, modResultFileName)
        val modResultOS = new FileOutputStream(modResultFile)

        if(selects.contains(modResultFileName)) {
          val inModResultModel = ModelFactory.createDefaultModel()
          val outModResultModel = ModelFactory.createDefaultModel()

          val modResultStreamRDF = new RewriteIRIStreamWrapper(
            StreamRDFLib.graph(outModResultModel.getGraph),
            regex = s"^${oldPlainBase}/?",
            baseURI.replaceAll("[^/]$", "/") + modName + databusPath + "/")

          RDFDataMgr.read(inModResultModel,remoteIS,modResultURI.toString,Lang.TTL)
          inModResultModel.getGraph.find().foreach({
            triple => modResultStreamRDF.triple(triple)
          })

          combinedModel.add(outModResultModel)
          RDFDataMgr.write(modResultOS,outModResultModel,RDFFormat.TURTLE_BLOCKS)
        } else {
          IOUtils.copy(remoteIS, modResultOS)
        }
        remoteIS.close()
        modResultOS.close()
    })

    combinedModel.add(metadataModel)
    vosService.addNamedModel(me.task.getDatabusFile.getDataIdSingleFile + "#" + me.task.getMod.getName, combinedModel, replace = true)
  }

  //  def add(task: Task, response: MetadataResponse): Unit = {
  //
  //  }
  //
  //  def get(task: Task): MetadataExtension = {
  //    val modName = task.getMod.getName
  //    val databusFile = task.getDatabusFile.getDataIdSingleFile
  //
  //    MetadataExtension(new URI(""), new HashMap[String,URI]())
  //  }
  //
  //  def update(task: Task, response: MetadataResponse): Unit = {
  //
  //  }
  //
  //  def remove(task: Task): Unit = {
  //
  //  }
}
