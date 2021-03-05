package org.dbpedia.databus_mods.server.core.service

import java.io.File

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Handle physical singleFiles
 * TODO
 */
@Service
class FileService(@Value("${tmp.volume}") base: String) {

  private lazy val baseFile: File = new File(base)

  def getOrCreate(modName: String, databusPath: String, extension: String = "metadata.ttl"): File = {
    val file = new File(base, List(modName, databusPath, `extension`).mkString("/"))
    file.getParentFile.mkdirs()
    file
  }

  def listDir(modName: String, databusPath: String) = {
    val dir = new File(base+ "/" + modName + "/" +databusPath)
    dir.listFiles()
  }
}
