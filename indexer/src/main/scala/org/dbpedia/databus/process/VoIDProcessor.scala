package org.dbpedia.databus.process
import java.io.{BufferedInputStream, BufferedWriter, FileInputStream, FileWriter}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.riot.{Lang, RDFDataMgr}
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

    val resultAsTurtle = writeResultAsTurtle(item, result._1, result._2)

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
  def calculateVoIDPartitions(iter:PipedRDFIterator[Triple]): (List[String],Map[String,Int])={

    val rdfType = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
    val classPartitionSeq: ListBuffer[String] = ListBuffer.empty
    val propertyPartitionSeq: ListBuffer[String] = ListBuffer.empty

    while(iter.hasNext){
      val triple = iter.next()
      if(triple.predicateMatches(rdfType)) classPartitionSeq+=triple.getObject.getURI
      else propertyPartitionSeq+=triple.getPredicate.getURI
    }

    val distinctClassPartitions = classPartitionSeq.toList.distinct
    val groupedPropertiesMap = propertyPartitionSeq.groupBy(identity).mapValues(_.size)

    (distinctClassPartitions, groupedPropertiesMap)
  }

  /**
    * write out result as turtle
    *
    * @param item item of the processed file
    * @param classPartitionsList list of void:classPartitions
    * @param propertyPartitionsMap map of void:PropertyPartitions with number of occurrences
    * @return turtle string
    */
  def writeResultAsTurtle(item:Item, classPartitionsList:List[String], propertyPartitionsMap:Map[String,Int]):String ={
    var result = new StringBuilder(
      s"<${item.distribution}> <http://dataid.dbpedia.org/ns/core#file> <${item.file}>;\n" +
        s"\t<http://dataid.dbpedia.org/ns/core#version> <${item.version}> ;\n")

    classPartitionsList.foreach(x=> {
      result++=s"\t<http://rdfs.org/ns/void#classPartition> [ <http://rdfs.org/ns/void#class> <$x>; ] ;\n"
    })

    propertyPartitionsMap.foreach(x=>{
      result++=s"\t<http://rdfs.org/ns/void#propertyPartition> [ \n\t\t<http://rdfs.org/ns/void#property> <${x._1}>; \n\t\t<http://rdfs.org/ns/void#triples> ${x._2}\n\t] ;\n"
    })

//    val count = classPartitionSeq.size + propertyPartitionSeq.size
//    result ++= s"\t<http://rdfs.org/ns/void#triples> $count"

    val resultFile = File("./mappings_2020.04.01.ttl")
    val bw = new BufferedWriter(new FileWriter(resultFile.toJava, true))

    val resultStr = result.replace(result.lastIndexOf(";"),result.size,".").mkString

    bw.append(resultStr)
    bw.append("\n")
    bw.close()

    resultStr
  }
}
