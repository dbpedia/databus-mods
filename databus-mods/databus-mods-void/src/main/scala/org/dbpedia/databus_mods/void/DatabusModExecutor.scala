package org.dbpedia.databus_mods.void

import java.util.Calendar

import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.rdf.model.{ModelFactory, ResourceFactory}
import org.apache.jena.riot.lang.PipedRDFIterator
import org.dbpedia.databus_mods.lib.util.RdfFileHelpers
import org.dbpedia.databus_mods.lib.{AbstractDatabusModExecutor, DatabusModInput}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable

@Service
class DatabusModExecutor @Autowired()(config: Config) extends AbstractDatabusModExecutor {

  private val log = LoggerFactory.getLogger(classOf[DatabusModExecutor])
  private implicit val basePath: String = config.volumes.localRepo
  private val modName = "VoIDMod"

  /**
    * Probe mimeType and compression type of a file
    *
    * @param databusModInput mod input data
    */
  def process(databusModInput: DatabusModInput): Unit = {
    try {
      log.info(s"process ${databusModInput.id}")
      val metadataFile = databusModInput.modMetadataFile

      val iter = RdfFileHelpers.readAsTriplesIterator(databusModInput.file)

      try {
        if (iter.hasNext) {
          val result = calculateVoIDPartitions(iter)
          val classPartitionsMap = result._1
          val propertyPartitionsMap = result._2
          writeResultsToFiles(databusModInput, classPartitionsMap, propertyPartitionsMap)
        } else {
          databusModInput.modErrorFile.parent.createDirectories()
          databusModInput.modErrorFile.write(
            Calendar.getInstance().getTime.toString + "\n" +
              "empty triple iterator"
          )
        }
      } catch {
        case riotExpection: org.apache.jena.riot.RiotException => println("iterator empty")
      }
    }
    catch {
      case e: Exception =>
        log.error(s"failed to process ${databusModInput.id}")
        databusModInput.modErrorFile.parent.createDirectories()
        databusModInput.modErrorFile.write(
          Calendar.getInstance().getTime.toString + "\n" +
            e.getStackTrace.mkString("\n")
        )
    }
  }

  /**
    * calculate void:classPartition and void:propertyPartition together with its occurrences
    *
    * @param iter iterator of RDF Triples
    * @return Tuple of classPartitionList and Map of propertyPartitions together with its number of occurrences
    */
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

  /**
    * increase number of occurrences, or add element if not exists yet
    *
    * @param anyMap map
    * @param elem   element to check
    */
  def increaseCountOrAddToMapIfNotExists(anyMap: mutable.HashMap[String, Int], elem: String): Unit = {
    anyMap.get(elem) match {
      case Some(count) => anyMap.update(elem, count + 1)
      case None => anyMap.put(elem, 1)
    }
  }


  /**
    * write result into jena model
    *
    * @param databusModInput input data on which calculations were carried out
    * @param classPartitionsMap
    * @param propertyPartitionsMap
    */
  def writeResultsToFiles(databusModInput: DatabusModInput, classPartitionsMap: mutable.HashMap[String, Int], propertyPartitionsMap: mutable.HashMap[String, Int]): Unit = {

    val externalResultFile = databusModInput.modMetadataFile(basePath).parent / "externalResult.ttl"
    val externalResultModel = ModelFactory.createDefaultModel()

    val modelHelper = new org.dbpedia.databus_mods.lib.util.DatabusModOutputHelper(
      databusModInput,
      config.volumes.localRepo, modName,
      Some(externalResultFile)
    )
    val resultURI = modelHelper.getResultURI()

    //feed and write out external result model
    externalResultModel.setNsPrefix("void", "http://rdfs.org/ns/void#")

    // enable all SimpleNodeWrapper implicit conversions
    import org.dbpedia.databus_mods.lib.util.SimpleNodeWrapper.implicits._

    classPartitionsMap.foreach(x => {
      val blankNode = ResourceFactory.createResource()
      modelHelper.addStmtToModel(resultURI, "http://rdfs.org/ns/void#classPartition", blankNode, externalResultModel)
      modelHelper.addStmtToModel(blankNode, "http://rdfs.org/ns/void#class", x._1, externalResultModel)
      modelHelper.addStmtToModel(blankNode, "http://rdfs.org/ns/void#triples", x._2, externalResultModel)
    })

    propertyPartitionsMap.foreach(x => {
      val blankNode = ResourceFactory.createResource()
      modelHelper.addStmtToModel(resultURI, "http://rdfs.org/ns/void#propertyPartition", blankNode, externalResultModel)
      modelHelper.addStmtToModel(blankNode, "http://rdfs.org/ns/void#property", x._1, externalResultModel)
      modelHelper.addStmtToModel(blankNode, "http://rdfs.org/ns/void#triples", x._2, externalResultModel)
    })

    modelHelper.writeModel(externalResultModel, externalResultFile)

    //write out meta data
    modelHelper.writeMetaDataModels()
  }
}