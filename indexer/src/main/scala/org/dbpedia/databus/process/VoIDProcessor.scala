package org.dbpedia.databus.process
import java.io.{BufferedInputStream, FileInputStream}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.{NodeFactory, Triple}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable.ListBuffer

class VoIDProcessor extends Processor {

  override def process(file: File, item: Item, sink: Sink): Unit = {
    val bis = new BufferedInputStream(new FileInputStream(file.toJava))

    val in = Compressor.decompress(bis)

    val iter:PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
    val rdfStream:PipedRDFStream[Triple] = new PipedTriplesStream(iter)

    val rdfType = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
    val classpartitionSeq: ListBuffer[String] = ListBuffer.empty
    val propertyPartitionSeq: ListBuffer[String] = ListBuffer.empty

    // PipedRDFStream and PipedRDFIterator need to be on different threads
    val executor:ExecutorService = Executors.newSingleThreadExecutor()

    // Create a runnable for our parser thread
    val parser:Runnable = new Runnable() {
      override def run() {
        // Call the parsing process.
        RDFDataMgr.parse(rdfStream, in, Lang.TTL)
      }
    }

    // Start the parser on another thread
    executor.submit(parser)


    while(iter.hasNext){
      val triple = iter.next()
      if(triple.predicateMatches(rdfType)) classpartitionSeq+=triple.getObject.toString
      else propertyPartitionSeq+=triple.getPredicate.toString
    }

    var result = new StringBuilder(s"item.downloadURL a void:Dataset;\n")

    classpartitionSeq.toList.distinct.foreach(x=> {
      result++=s"void:classPartition [ void:class $x; ];\n"
    })

    propertyPartitionSeq.toList.distinct.foreach(x=>{
      result++=s"void:propertyPartition [ void:property $x; ];\n"
    })

    sink.consume(result.mkString("","","."))
  }
}
