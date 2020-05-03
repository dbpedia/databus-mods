package org.dbpedia.databus.process
import java.io._
import java.nio.file.Files

import better.files.File
import org.apache.commons.compress.utils.IOUtils
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink


class MimeTypeProcessor extends Processor {

  /**
    * Probe mimeType and compression type of a file
    *
    * @param file file to probe
    * @param item file-corresponding item
    * @param sink sink
    */
  override def process(file:File, item: Item, sink: Sink): Unit = {

    val preparedFile:File = {
      val inStream = Compressor.decompress(new BufferedInputStream(new FileInputStream(file.toJava)))

      if(inStream.getClass.getCanonicalName != "java.io.BufferedInputStream") {
        val decompressedFile = file.parent/ s"${file.nameWithoutExtension(includeAll = false)}"
        copyStream(inStream,new FileOutputStream(decompressedFile.toJava))


        sink.consume(s"${checkMimeType(file)} compression for file $file ")
        decompressedFile
      }
      else file
    }

    val mimetype = checkMimeType(preparedFile)
    item.mimetype = mimetype
    sink.consume(s"$mimetype mimetype for file $file")
  }

  /**
    * Check mimeType of file
    *
    * @param file file to check mimeType from
    * @return mimetype
    */
  def checkMimeType(file: File): String = {
    Files.probeContentType(file.path)
  }

  /**
    * Copy input stream to output stream
    *
    * @param in input stream
    * @param out output stream
    */
  def copyStream(in: InputStream, out: OutputStream): Unit = {
    try {
      IOUtils.copy(in, out)
    }
    finally if (out != null) {
      out.close()
    }
  }
}
