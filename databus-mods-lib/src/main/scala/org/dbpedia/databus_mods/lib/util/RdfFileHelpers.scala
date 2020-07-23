package org.dbpedia.databus_mods.lib.util

import java.io.{BufferedInputStream, FileInputStream}

import better.files.File
import org.apache.jena.graph.Triple
import org.apache.jena.riot.{Lang, RDFParser}
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
//import org.dice_research.rdfdetector.RdfSerializationDetector
import scala.collection.JavaConversions._

object RdfFileHelpers {

  /**
    * read rdf file to triples iterator
    *
    * @param file local file to process
    * @return triples iterator
    */
  def readAsTriplesIterator(file: File): PipedRDFIterator[Triple] = {

    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
    val in = Compressor.decompress(bis)
//    val detector = new RdfSerializationDetector();

    val lang = "NTRIPLES"; //org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)
    var iter :PipedRDFIterator[Triple] = null
    //    for (lang <- detector.detect(bis))
    //    {
    iter = new PipedRDFIterator[Triple]()
    val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(iter)

    RDFParser.source(in).lang(Lang.NTRIPLES).base("http://base").parse(rdfStream)
    //    }

    iter
  }

}
