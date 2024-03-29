package org.dbpedia.databus.mods.core.io

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

import java.io.{File, FileInputStream, InputStream}
import java.net.URI

object DataUtil {

  def openStream(uri: URI): InputStream = {
    // TODO
    val is = uri.getScheme match {
      case "http" | "https" =>
        val client = HttpClientBuilder.create().build()
        val respon = client.execute(new HttpGet(uri))
        respon.getEntity.getContent
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
