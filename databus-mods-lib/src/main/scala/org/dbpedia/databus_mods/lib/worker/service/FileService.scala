package org.dbpedia.databus_mods.lib.worker.service

import better.files.File
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class FileService(@Value("${worker.volume}") volume: String) {

//  private lazy val baseFile: File = new File("./target/mod-data")
//
//  def getOrCreate(databusPath: String, extension: String = "metadata.ttl"): File = {
//    val file = new File(baseFile,List(databusPath,`extension`).mkString("/"))
//    file.getParentFile.mkdirs()
//    file
//  }

  def createFile(dataIdFilePath: String, metadata: String = "metadata.ttl"): java.io.File = {
    val file = File(volume) / dataIdFilePath.replaceFirst("^/","") / metadata
    file.createFileIfNotExists(createParents = true)
    file.toJava
  }

  def findFile(dataIdFilePath: String, metadata: String = "metadata.ttl"): Option[File] = {
    val file = File(volume) / dataIdFilePath.replaceFirst("^/","") / metadata
    if (file.exists) {
      Some(file)
    } else {
      None
    }
  }

  def listDir(path: String): List[File] = {
    val dir = File(volume) / path
    dir.list.toList
  }

  def delete(databusPath: String): Unit = {
    val file = File(volume) / databusPath
    FileUtils.deleteDirectory(file.toJava)
  }
}
