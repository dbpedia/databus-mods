package org.dbpedia.databus_mods.server

import java.io.{File, FileInputStream, FileOutputStream}

import org.apache.jena.graph
import org.apache.jena.graph.NodeFactory
import org.apache.jena.riot.system.{StreamRDF, StreamRDFLib, StreamRDFWrapper}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.scalatest.funsuite.AnyFunSuite

class Tests extends AnyFunSuite {

  test("rewrite IRIs in mod.ttl") {

    val modTTL = "/home/marvin/src/github.com/dbpedia/databus-mods/databus-mods-server/volumes/VoidVocab/dbpedia/databus/databus-data/2020.03.22/databus-data.nt.bz2/mod.ttl"

    val in = new FileInputStream(new File(modTTL))
    val oldBase = "file:///workspace/localRepo/"
    val modName = "VoidVocab"
    val newBase = "http://localhost:8901/" + modName + "/"

    val newFile = new File("/home/marvin/src/github.com/dbpedia/databus-mods/databus-mods-server/volumes/www/" +
      modName +
      "dbpedia/databus/databus-data/2020.03.22/databus-data.nt.bz2/mod.ttl")

    newFile.getParentFile.mkdirs()

    val outputStream = new FileOutputStream(newFile)
    val rewritten = new BaseRewriteStreamWrapper(StreamRDFLib.writer(outputStream), oldBase, newBase)
    RDFDataMgr.parse(rewritten, in, oldBase, Lang.TURTLE)
  }
}

class BaseRewriteStreamWrapper(streamRDF: StreamRDF,
                               oldBase: String,
                               newBase: String) extends StreamRDFWrapper(streamRDF) {

  override def triple(triple: graph.Triple): Unit = {

    other.triple(
      graph.Triple.create(
        if (triple.getSubject.isURI && triple.getSubject.getURI.startsWith(oldBase))
          NodeFactory.createURI(triple.getSubject.getURI.replace(oldBase, newBase))
        else
          triple.getSubject,
        triple.getPredicate,
        if (triple.getObject.isURI && triple.getObject.getURI.startsWith(oldBase))
          NodeFactory.createURI(triple.getObject.getURI.replace(oldBase, newBase))
        else
          triple.getObject
      )
    )
  }
}
