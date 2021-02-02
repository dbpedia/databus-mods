package org.dbpedia.databus_mods.server.web_ui

import java.io.{File, FileOutputStream}

import org.apache.jena.rdf.model.Model
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class Repo(@Value("${tmp.volume}") volume: String) {

  val baseDir = new File(volume)

  def save(model: Model, databusIdPath: String, result: String): Unit = {
    val resultFile = new File(baseDir, databusIdPath + "/" + result)
    resultFile.getParentFile.mkdirs()
    model.write(new FileOutputStream(resultFile), "TURTLE")
  }

  def listFiles(segment: String): Array[File] = {
    println(new File(baseDir, segment).getAbsolutePath)
    new File(baseDir, segment).listFiles()
  }

  def getFile(databusIdPath: String, result: String): Option[File] = {
    val resultFile = new File(baseDir, databusIdPath + "/" + result)
    if (resultFile.exists()) {
      Some(resultFile)
    } else {
      None
    }
  }
}
