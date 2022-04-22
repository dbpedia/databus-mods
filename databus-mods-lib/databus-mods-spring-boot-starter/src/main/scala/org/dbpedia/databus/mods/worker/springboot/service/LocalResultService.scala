package org.dbpedia.databus.mods.worker.springboot.service

import org.apache.commons.io.FileUtils
import org.dbpedia.databus.dataid.Part

import java.io.{File, InputStream, OutputStream}
import scala.util.Try

class LocalResultService(baseDir: File) extends ResultService {

  FileUtils.createParentDirectories(baseDir)

  override def openResultInputStream(didPart: Part, resultName: String): Option[InputStream] = {

    val resultFile = new File(baseDir, didPart.uriPath + "/" + resultName)
    if (resultFile.exists()) {
      Some(FileUtils.openInputStream(resultFile))
    } else {
      None
    }
  }

  override def openResultOutputStream(didPart: Part, resultName: String): Option[OutputStream] = {

    Try {
      val resultFile = new File(baseDir, didPart.uriPath + "/" + resultName)
      FileUtils.createParentDirectories(resultFile)
      resultFile.createNewFile()
      FileUtils.openOutputStream(resultFile)
    }.toOption
  }
}
