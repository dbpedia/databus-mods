package org.dbpedia.databus.process

import java.io.{BufferedInputStream, BufferedWriter, FileInputStream, FileWriter}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.Triple
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable

class SPOProcessor extends Processor {

  override def process(file: File, item: Item, sink: Sink): Unit = {
    val resultFile = File(s"./spoResults.csv")

    val iter = readAsTriplesIterator(file,item)

    val spo = calculateSPO(iter)

    writeResult(resultFile, spo._1, spo._2, spo._3)
  }

  def calculateSPO(iter:PipedRDFIterator[Triple]):(mutable.HashMap[String,Int],mutable.HashMap[String,Int],mutable.HashMap[String,Int])={
    val subjectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
    val predicateMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
    val objectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty

    while (iter.hasNext) {
      val triple = iter.next()
      val subj = {
        if(triple.getSubject.isURI) triple.getSubject.getURI
        else ""
      }
      val obj = {
        if(triple.getObject.isURI) triple.getObject.getURI
        else ""
      }
      val pre = triple.getPredicate.getURI

      if(subj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(subjectMap,subj)
      increaseCountIfExistsOrAddToMapIfNotExists(predicateMap,pre)
      if(obj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(objectMap,obj)
    }

    (subjectMap,predicateMap,objectMap)
  }

  def increaseCountIfExistsOrAddToMapIfNotExists(anyMap:mutable.HashMap[String,Int], elem:String):Unit ={
    anyMap.get(elem) match {
      case Some(count) => anyMap.update(elem, count+1)
      case None => anyMap.put(elem,1)
    }
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


  def writeResult(resultFile:File, subjectMap:mutable.HashMap[String,Int], predicateMap:mutable.HashMap[String,Int], objectMap:mutable.HashMap[String,Int]):Unit={
    val bw = new BufferedWriter(new FileWriter(resultFile.toJava, true))
//      bw.append("uri;type; count\n")

    write(subjectMap, "subject")
    write(predicateMap, "predicate")
    write(objectMap, "object")

    def write(myMap:mutable.HashMap[String,Int], spo:String):Unit={
      while(myMap.nonEmpty) {
        if (myMap.head._1.contains(";")) bw.append(s""""${myMap.head._1}";$spo;${myMap.head._2}\n""")
        else bw.append(s"${myMap.head._1};$spo;${myMap.head._2}\n")
        myMap.remove(myMap.head._1)
      }
    }

    bw.close()
  }

}
