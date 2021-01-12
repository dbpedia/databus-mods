package org.dbpedia.databus_mods.void.gtd

import org.apache.jena.graph.NodeFactory
import org.apache.jena.graph.Triple
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.riot.lang.PipedRDFIterator

import scala.collection.mutable

object VoIDUtil {

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

    import ModelUtil.ModelWrapper

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
