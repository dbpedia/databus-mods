package org.dbpedia.databus_mods.lib.util

import java.io.{BufferedInputStream, InputStream}

import org.apache.jena.graph.Triple
import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
import org.apache.jena.riot.{Lang, RDFParser}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor

object IORdfUtil {

  def toPipedRDF(inputStream: InputStream): PipedRDFIterator[Triple] = {
    val bis = new BufferedInputStream(inputStream)
    val in = Compressor.decompress(bis)

    //    val lang = org.dbpedia.databus.util.MimeTypeGetter.getRDFFormat(item.downloadURL)
    val pipedRDF: PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
    val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(pipedRDF)

    // PipedRDFStream and PipedRDFIterator need to be on different threads
    //    val executor: ExecutorService = Executors.newSingleThreadExecutor()

    // Create a runnable for our parser thread
    val parser: Runnable = new Runnable() {
      override def run() {
        // Call the parsing process.
        RDFParser.source(in).lang(Lang.NTRIPLES).base("http://base").parse(rdfStream)
      }
    }

    // Start the parser on another thread
    new Thread(parser).start()

    pipedRDF
  }
}
