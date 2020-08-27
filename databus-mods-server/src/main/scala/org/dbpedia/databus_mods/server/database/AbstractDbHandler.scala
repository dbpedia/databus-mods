package org.dbpedia.databus_mods.server.database

import org.dbpedia.databus_mods.server.DatabusFileStatus.DatabusFileStatus

abstract class AbstractDbHandler {

  def tableNameHash(name: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    new sun.misc.BASE64Encoder().encode(md.digest(name.getBytes))
  }

  def createDatabusFilesTable(): Unit

  def insertDatabusFile(databusFile: DatabusFile): Boolean

  def getDatabusFileById(fileId: String): Option[DatabusFile]

  def databusFilesByModNameAndStatus(modName: String, status: JobStatus.Value): List[DatabusFile]

  def createModProcessTable(modName: String): Unit

  def addJob(modName: String, id: String, status: JobStatus.Value = JobStatus.OPEN): Boolean

  def updateJobStatus(modName: String, id: String, status: JobStatus.Value)

  def checkOverallStatus(id: String, modNames: Array[String]): Array[Int]

  def updateDatabusFileStatus(id: String, status: DatabusFileStatus)

}

