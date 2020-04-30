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

import scala.collection.mutable

class SPOProcessor extends Processor {

  override def process(file: File, item: Item, sink: Sink): Unit = {
    val dir = File("./spoResults")
    dir.createDirectoryIfNotExists()

    val resultFile = dir / s"./${file.nameWithoutExtension(true)}_spoResult.csv"
    resultFile.delete(true)

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
      val subj = triple.getSubject.toString
      val obj = {
        if (triple.getObject.isLiteral) triple.getObject.getLiteralLexicalForm
        else if(triple.getObject.isURI) triple.getObject.getURI
        else triple.getObject.toString
      }
      val pre = triple.getPredicate.getURI

      println(triple)
      subjectMap.get(subj) match {
        case Some(count) => subjectMap.update(subj, count+1)
        case None => subjectMap.put(subj,1)
      }
      predicateMap.get(pre) match {
        case Some(count) => predicateMap.update(pre, count+1)
        case None => predicateMap.put(pre,1)
      }
      objectMap.get(obj) match {
        case Some(count) => objectMap.update(obj, count+1)
        case None => objectMap.put(obj,1)
      }
    }

    (subjectMap,predicateMap,objectMap)
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


  def writeResult(resultFile:File, subjectMap:mutable.HashMap[String,Int], predicateMap:mutable.HashMap[String,Int], objectMap:mutable.HashMap[String,Int])={
    val bw = new BufferedWriter(new FileWriter(resultFile.toJava, true))
    bw.append("subjects, countSubject, predicates, countPredicate, objects, countObject\n")

    while(subjectMap.nonEmpty || objectMap.nonEmpty || predicateMap.nonEmpty){
      val str = StringBuilder.newBuilder

      if (subjectMap.nonEmpty) {
        if (subjectMap.head._1.contains(";")) str.append(s""""${subjectMap.head._1}";${subjectMap.head._2};""") //escape semicolon
        else str.append(s"${subjectMap.head._1};${subjectMap.head._2};")
        subjectMap.remove(subjectMap.head._1)
      }
      else str.append(";;")

      if(predicateMap.nonEmpty) {
        if (predicateMap.head._1.contains(";")) str.append(s""""${predicateMap.head._1}";${predicateMap.head._2};""")
        else str.append(s"${predicateMap.head._1};${predicateMap.head._2};")
        predicateMap.remove(predicateMap.head._1)
      }
      else str.append(";;")

      if(objectMap.nonEmpty) {
        if (objectMap.head._1.contains(";")) str.append(s""""${objectMap.head._1}";${objectMap.head._2}""".stripMargin)
        else  str.append(s"${objectMap.head._1};${objectMap.head._2}")
        objectMap.remove(objectMap.head._1)
      }
      else str.append(";")

      str.append("\n")

      println(str)
      bw.append(str)
    }

    bw.close()
  }

}
