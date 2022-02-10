package org.dbpedia.databus.mods.core.util

import org.apache.commons.compress.compressors.{CompressorException, CompressorStreamFactory}

import java.io.{BufferedInputStream, InputStream}

object Compressor {
  def decompress(bis: BufferedInputStream): InputStream = {
    //Welche Funktion hat actualDecompressConcatenated?
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
