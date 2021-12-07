package lib.util

import java.io.{BufferedInputStream, FileInputStream}
import java.util.concurrent.{ExecutorService, Executors}

import better.files.File
import org.apache.jena.graph.Triple
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.apache.jena.riot.{Lang, RDFParser}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
//import org.dice_research.rdfdetector.RdfSerializationDetector

object RdfFileHelpers {

  /**
    * read rdf file to triples iterator
    *
    * @param file local file to process
    * @return triples iterator
    */
  def readAsTriplesIterator(file: File): PipedRDFIterator[Triple] = {

    //    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
    //    val in = Compressor.decompress(bis)
    //    //    val detector = new RdfSerializationDetector();
    //
    //    val lang = "NTRIPLES"; //org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)
    //    var iter: PipedRDFIterator[Triple] = null
    //    //    for (lang <- detector.detect(bis))
    //    //    {
    //    iter = new PipedRDFIterator[Triple]()
    //    val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(iter)
    //
    //    RDFParser.source(in).lang(Lang.TURTLE).base("http://base").parse(rdfStream)
    //    //    }
    //
    //    iter


    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
    val in = Compressor.decompress(bis)

    //    val lang = org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)
    val iter: PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
    val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(iter)

    // PipedRDFStream and PipedRDFIterator need to be on different threads
    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // Create a runnable for our parser thread
    val parser: Runnable = new Runnable() {
      override def run() {
        // Call the parsing process.
        RDFParser.source(in).lang(Lang.NTRIPLES).base("http://base").parse(rdfStream)
      }
    }

    // Start the parser on another thread
    executor.submit(parser)

    iter
  }

}
