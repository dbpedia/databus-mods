package org.dbpedia.databus_mods.lib.util

import java.io.{File, FileInputStream, InputStream}
import java.net.URI

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

// TODO visitor pattern
object UriUtil {

  def openStream(uri: URI): InputStream = {

    val is = uri.getScheme match {
      case "http" | "https" => uri.toURL.openStream()
      case "file" => new FileInputStream(new File(uri))
      case _ => throw new Exception("URI scheme not supported")
    }

    uri.toString.split("\\.").last match {
      case "bz2" => new BZip2CompressorInputStream(is)
      case "gz" => new GzipCompressorInputStream(is)
      case _ => is
    }
  }
}
