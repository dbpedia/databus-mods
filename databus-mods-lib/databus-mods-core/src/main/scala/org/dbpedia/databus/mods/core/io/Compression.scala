package org.dbpedia.databus.mods.core.io

import org.apache.commons.compress.compressors.{CompressorException, CompressorStreamFactory}

import java.io.{BufferedInputStream, InputStream}

object Compression {

  def decompress(bis: BufferedInputStream): InputStream = {
    // TODO what does actualDecompressConcatenated?
    try {

      new CompressorStreamFactory().createCompressorInputStream(
        CompressorStreamFactory.detect(bis),
        bis,
        true
      )

    } catch {

      case _: CompressorException =>
        System.err.println(s"[WARN] No compression found for input stream - raw input")
        bis

      case unknown: Throwable => println("[ERROR] Unknown exception: " + unknown)
        bis
    }
  }
}
