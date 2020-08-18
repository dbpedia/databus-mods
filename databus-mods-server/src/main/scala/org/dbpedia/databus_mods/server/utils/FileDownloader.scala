package org.dbpedia.databus_mods.server.utils

import java.net.URL

import better.files.File
import org.dbpedia.databus_mods.server.database.DatabusFile
import org.slf4j.LoggerFactory

import sys.process._

object FileDownloader {

  private val log = LoggerFactory.getLogger(classOf[File])

  def getLocalFile(baseDir: File, databusFile: DatabusFile): File = {
    val file =
      baseDir / databusFile.publisher / databusFile.group / databusFile.artifact / databusFile.version / databusFile.fileName / databusFile.sha256sum
    file.parent.createDirectories()
    file
  }

  // TODO smooth
  def toFileIfNotExits(url: URL, file: File): Option[File] = {
    if (!file.exists) {
      log.info(s"download - $url")
      if (url.#>(file.toJava).! != 0) {
        log.error(s"failed download - $url")
        None
      }
      else {
        log.info(s"finished download - ${file.size} bytes - $url")
        Some(file)
      }
    } else {
      Some(file)
    }
  }
}
