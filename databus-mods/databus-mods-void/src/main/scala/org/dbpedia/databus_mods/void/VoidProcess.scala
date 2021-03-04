package org.dbpedia.databus_mods.void

import java.net.URI

import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.riot.lang.PipedRDFIterator
import org.apache.jena.vocabulary.RDF
import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper
import org.dbpedia.databus_mods.lib.util.{IORdfUtil, UriUtil}
import org.dbpedia.databus_mods.lib.worker.execution.{Extension, ModProcessor}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import scala.collection.mutable

@Component
class VoidProcess extends ModProcessor {

  private val log = LoggerFactory.getLogger(classOf[VoidProcess])

  def process(ext: Extension): Unit = {
    val is = UriUtil.openStream(new URI(ext.source))
    val pipedRDF = IORdfUtil.toPipedRDF(is)
    ext.getModel.addStmtToModel("", RDF.`type`.getURI, "https://mods.tools.dbpedia.org/ns/void#VoidMod")
    try {
      if (pipedRDF.hasNext) {
        val (classPartitionMap, propertyPartitionMap) = calculateVoIDPartitions(pipedRDF)
        val voidModel = toJenaModel(classPartitionMap, propertyPartitionMap)
        voidModel.setNsPrefix("void", "http://rdfs.org/ns/void#")
        voidModel.write(ext.createModResult("rdfVoid.ttl"), "TURTLE")
      } else {
        log.warn(s"empty iterator")
      }
    }
  }

  def calculateVoIDPartitions(iter: PipedRDFIterator[Triple]): (mutable.HashMap[String, Int], mutable.HashMap[String, Int]) = {

    val rdfType = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

    val classPartitionMap: mutable.HashMap[String, Int] = mutable.HashMap.empty
    val propertyPartitionMap: mutable.HashMap[String, Int] = mutable.HashMap.empty

    while (iter.hasNext) {
      val triple = iter.next()
      increaseCountOrAddToMapIfNotExists(propertyPartitionMap, triple.getPredicate.getURI)
      if (triple.predicateMatches(rdfType)) {
        if (triple.getObject.isURI) increaseCountOrAddToMapIfNotExists(classPartitionMap, triple.getObject.getURI)
      }
    }
    (classPartitionMap, propertyPartitionMap)
  }

  private def increaseCountOrAddToMapIfNotExists(anyMap: mutable.HashMap[String, Int], elem: String): Unit = {
    anyMap.get(elem) match {
      case Some(count) => anyMap.update(elem, count + 1)
      case None => anyMap.put(elem, 1)
    }
  }

  def toJenaModel(
                   classPartitionsMap: mutable.HashMap[String, Int],
                   propertyPartitionsMap: mutable.HashMap[String, Int],
                   uri: String = "")
  : Model = {

    import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper

    val model = ModelFactory.createDefaultModel()

    classPartitionsMap.foreach(x => {
      val blankNode = ResourceFactory.createResource()
      model.addStmtToModel(uri, "http://rdfs.org/ns/void#classPartition", blankNode)
      model.addStmtToModel(blankNode, "http://rdfs.org/ns/void#class", x._1)
      model.addStmtToModel(blankNode, "http://rdfs.org/ns/void#triples", x._2)
    })

    propertyPartitionsMap.foreach(x => {
      val blankNode = ResourceFactory.createResource()
      model.addStmtToModel(uri, "http://rdfs.org/ns/void#propertyPartition", blankNode)
      model.addStmtToModel(blankNode, "http://rdfs.org/ns/void#property", x._1)
      model.addStmtToModel(blankNode, "http://rdfs.org/ns/void#triples", x._2)
    })

    model
  }

}
