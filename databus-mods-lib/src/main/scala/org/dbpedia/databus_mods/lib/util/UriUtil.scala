package org.dbpedia.databus_mods.lib.util

import java.io.{File, FileInputStream, InputStream}
import java.net.URI

// TODO visitor pattern
object UriUtil {

  def openStream(uri: URI): InputStream = {

    uri.getScheme match {
      case "http" | "https" => uri.toURL.openStream()
      case "file" => new FileInputStream(new File(uri))
      case _ => throw new Exception("URI scheme not supported")
    }
  }
}
