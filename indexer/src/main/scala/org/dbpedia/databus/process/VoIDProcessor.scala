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
import java.io.{BufferedInputStream, BufferedWriter, FileInputStream, FileWriter}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable.ListBuffer

/**
  * calculate all void:propertyPartition together with its number of occurrences and the void:classPartition of a processed file
  * also see here: https://www.w3.org/TR/void/#class-property-partitions
  */
class VoIDProcessor extends Processor {

  /**
    * calculate all void:propertyPartition together with its number of occurrences and the void:classPartition of a processed file
    *
    * @param file local file to process
    * @param item item of processed file
    * @param sink sink result
    */
  override def process(file: File, item: Item, sink: Sink): Unit = {
    val iter = readAsTriplesIterator(file,item)

    val result = calculateVoIDPartitions(iter)
    val classPartitionsMap = result._1
    val propertyPartitionsMap = result._2

    val dir = File(s"./voidResults${item.version.toString.split("dbpedia.org").last}")
    dir.createDirectoryIfNotExists()
    val resultFile = dir / s"./${file.nameWithoutExtension(true)}.ttl"

    val resultAsTurtle = writeResultAsTurtle(item, classPartitionsMap, propertyPartitionsMap,resultFile)

    sink.consume(resultAsTurtle)

  }

  /**
    * read rdf file to triples iterator
    *
    * @param file local file to process
    * @param item related item of local file
    * @return triples iterator
    */
  def readAsTriplesIterator(file:File, item:Item):PipedRDFIterator[Triple] ={

    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
    val in = Compressor.decompress(bis)

    val lang = org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)

    val iter:PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
    val rdfStream:PipedRDFStream[Triple] = new PipedTriplesStream(iter)

    // PipedRDFStream and PipedRDFIterator need to be on different threads
    val executor:ExecutorService = Executors.newSingleThreadExecutor()

    // Create a runnable for our parser thread
    val parser:Runnable = new Runnable() {
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
  def calculateVoIDPartitions(iter:PipedRDFIterator[Triple]): (Map[String,Int],Map[String,Int])={

    val rdfType = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
    val classPartitionSeq: ListBuffer[String] = ListBuffer.empty
    val propertyPartitionSeq: ListBuffer[String] = ListBuffer.empty

    try{
      while(iter.hasNext){
        val triple = iter.next()
        propertyPartitionSeq+=triple.getPredicate.getURI
        if(triple.predicateMatches(rdfType)) {
          if(triple.getObject.isURI) classPartitionSeq+=triple.getObject.getURI
        }
      }
    } catch {
      case riotExpection:org.apache.jena.riot.RiotException=> println("iterator empty")
    }


    val groupedClassesMap = classPartitionSeq.groupBy(identity).mapValues(_.size)
    val groupedPropertiesMap = propertyPartitionSeq.groupBy(identity).mapValues(_.size)

    (groupedClassesMap, groupedPropertiesMap)
  }

  /**
    * write out result as turtle
    *
    * @param item item of the processed file
    * @param classPartitionsMap map of void:classPartitions with count occurrences
    * @param propertyPartitionsMap map of void:PropertyPartitions with number of occurrences
    * @return turtle string
    */
  def writeResultAsTurtle(item:Item, classPartitionsMap:Map[String,Int], propertyPartitionsMap:Map[String,Int], resultFile:File):String ={

    var result = new StringBuilder(
      s"<${item.distribution}> <http://dataid.dbpedia.org/ns/core#file> <${item.file}>;n" +
        s"\t<http://dataid.dbpedia.org/ns/core#version> <${item.version}> ;\n")

    classPartitionsMap.foreach(x=> {
      result++=s"\t<http://rdfs.org/ns/void#classPartition> [ \n\t\t<http://rdfs.org/ns/void#class> <${x._1}>; " +
        s"\n\t\t<http://rdfs.org/ns/void#triples> ${x._2}\n\t] ;\n"
    })

    propertyPartitionsMap.foreach(x=>{
      result++=s"\t<http://rdfs.org/ns/void#propertyPartition> [ \n\t\t<http://rdfs.org/ns/void#property> <${x._1}>; " +
        s"\n\t\t<http://rdfs.org/ns/void#triples> ${x._2}\n\t] ;\n"
    })

    val bw = new BufferedWriter(new FileWriter(resultFile.toJava, true))

    val resultStr = result.replace(result.lastIndexOf(";"),result.size,".").mkString

    bw.append(resultStr)
    bw.append("\n")
    bw.close()

    resultStr
  }
}
