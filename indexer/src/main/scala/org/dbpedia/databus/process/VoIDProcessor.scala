/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.process

import java.io.{BufferedInputStream, FileInputStream}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable

/**
  * calculate all void:propertyPartition together with its number of occurrences and the void:classPartition of a processed file
  * also see here: https://www.w3.org/TR/void/#class-property-partitions
  */
@SerialVersionUID(1L)
class VoIDProcessor extends Processor {

  val modName = "VoIdMod"

  /**
    * calculate all void:propertyPartition together with its number of occurrences and the void:classPartition of a processed file
    *
    * @param file local file to process
    * @param item item of processed file
    * @param sink sink result
    */
  override def process(file: File, item: Item, sink: Sink): Unit = {
    val iter = readAsTriplesIterator(file, item)

    try {
      if (iter.hasNext) {
        val result = calculateVoIDPartitions(iter)
        val classPartitionsMap = result._1
        val propertyPartitionsMap = result._2

        val model = writeResultToModel(item, classPartitionsMap, propertyPartitionsMap)

        sink.consume(item, model, modName)
      }
    } catch {
      case riotExpection: org.apache.jena.riot.RiotException => println("iterator empty")
    }

  }

  /**
    * read rdf file to triples iterator
    *
    * @param file local file to process
    * @param item related item of local file
    * @return triples iterator
    */
  def readAsTriplesIterator(file: File, item: Item): PipedRDFIterator[Triple] = {

    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
    val in = Compressor.decompress(bis)

    val lang = org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)
    val iter: PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
    val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(iter)

    // PipedRDFStream and PipedRDFIterator need to be on different threads
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // Create a runnable for our parser thread
    val parser: Runnable = new Runnable() {
      override def run() {
        // Call the parsing process.
        RDFDataMgr.parse(rdfStream, in, lang)
      }
    }

    // Start the parser on another thread
    executor.submit(parser)

    iter
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
    * write out result to jena model
    *
    * @param item                  item of the processed file
    * @param classPartitionsMap    map of void:classPartitions with count occurrences
    * @param propertyPartitionsMap map of void:PropertyPartitions with number of occurrences
    * @return rdf model
    */
  def writeResultToModel(item: Item, classPartitionsMap: mutable.HashMap[String, Int], propertyPartitionsMap: mutable.HashMap[String, Int]): Model = {

    import scala.collection.JavaConverters.mapAsJavaMapConverter
    val model: Model = ModelFactory.createDefaultModel()

    val prefixMap: Map[String, String] = Map(
      "myMod" -> "http://myservice.org/mimeType/repo/marvin/wikidata/instance-types/2019.08.01/",
      "myModVoc" -> "http://myservice.org/mimeType/repo/modvocab.ttl#",
      "void" -> "http://rdfs.org/ns/void#"
    )
    model.setNsPrefixes(prefixMap.asJava)

    val resultURI = s"${prefixMap("myMod")}${item.shaSum}.ttl#result"

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(resultURI),
        ResourceFactory.createProperty("http://dataid.dbpedia.org/ns/mod.ttl#resultDerivedFrom"),
        ResourceFactory.createResource(item.file.toString)))

    addMapToModel(model, classPartitionsMap, resultURI, false)
    addMapToModel(model, propertyPartitionsMap, resultURI)

    model
  }

  /**
    * add all elements of hash map (propertyPartitions and classPartitions) to model
    *
    * @param model        jena model
    * @param map          propertyPartitionsMap or classPartitionsMap
    * @param resultURI    URI of result
    * @param isPropertyMap
    */
  def addMapToModel(model: Model, map: mutable.HashMap[String, Int], resultURI: String, isPropertyMap: Boolean = true) = {
    map.foreach(x => {
      val blankNode = ResourceFactory.createResource()

      model.add(
        ResourceFactory.createStatement(
          ResourceFactory.createResource(resultURI),
          if (isPropertyMap) ResourceFactory.createProperty("http://rdfs.org/ns/void#propertyPartition")
          else ResourceFactory.createProperty("http://rdfs.org/ns/void#classPartition"),
          blankNode
        ))

      model.add(
        ResourceFactory.createStatement(
          blankNode,
          if (isPropertyMap) ResourceFactory.createProperty("http://rdfs.org/ns/void#property")
          else ResourceFactory.createProperty("http://rdfs.org/ns/void#class"),
          ResourceFactory.createResource(x._1)
        ))

      model.add(
        ResourceFactory.createStatement(
          blankNode,
          ResourceFactory.createProperty("http://rdfs.org/ns/void#triples"),
          ResourceFactory.createTypedLiteral(x._2)
        ))
    })
  }

}
