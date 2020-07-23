package org.dbpedia.databus_mods.lib.util

import java.io.BufferedInputStream
import java.net.URL

import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dice_research.rdfdetector.RdfSerializationDetector
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConversions._

class Tests extends AnyFunSuite {

  test("detect RDF ser.") {

    val detector = new RdfSerializationDetector()

    val is = new URL(
      "http://downloads.dbpedia.org/repo/dbpedia/generic/labels/2020.07.01/labels_lang%3dde.ttl.bz2"
    ).openStream()

    val langs = detector.detect(
      new BufferedInputStream(
        Compressor.decompress(
          new BufferedInputStream(
            is
          ))))

    is.close()
    langs.foreach(println)
  }
}
