package org.dbpedia.databus_mods.server.database.derby

import java.sql.{DriverManager, SQLException, Timestamp}

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException
import org.dbpedia.databus_mods.server.DatabusFileStatus.DatabusFileStatus
import org.dbpedia.databus_mods.server.database.{AbstractDbHandler, DatabusFile, DatabusFileIterator, JobStatus}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

class DerbyDbHandlerImpl(databaseUrl: String) extends AbstractDbHandler {

  private val log = LoggerFactory.getLogger(classOf[DerbyDbHandlerImpl])

  private val databusFileTableName = "databusFiles"

  def createDatabusFilesTable(): Unit = {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    try {
      val sql: String =
        s"""CREATE TABLE $databusFileTableName (
          |id          varchar(2000) primary key,
          |publisher   varchar(2000),
          |grp         varchar(2000),
          |artifact    varchar(2000),
          |version     varchar(2000),
          |fileName    varchar(2000),
          |sha256sum   varchar(128),
          |downloadUrl varchar(2000),
          |status      integer,
          |timestamp   timestamp
          |)""".stripMargin
      statement.execute(sql)
    } catch {
      case e: SQLException => log.warn(e.getMessage)
    }
    conn.close()
  }

  def createModProcessTable(modName: String): Unit = {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    try {
      val sql: String =
        s"""CREATE TABLE ${modName} (
           |id        varchar(2000) primary key,
           |status    integer,
           |timestamp timestamp
           |)""".stripMargin
      statement.execute(sql)
    } catch {
      case e: SQLException => log.warn(e.getMessage)
    }
    conn.close()
  }

  override def insertDatabusFile(databusFile: DatabusFile): Boolean = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    var notExists = true
    val sql =
      s"""
         |INSERT INTO $databusFileTableName VALUES (
         |'${databusFile.id}',
         |'${databusFile.publisher}',
         |'${databusFile.group}',
         |'${databusFile.artifact}',
         |'${databusFile.version}',
         |'${databusFile.fileName}',
         |'${databusFile.sha256sum}',
         |'${databusFile.downloadUrl}',
         |${databusFile.status.id},
         |'${new Timestamp(System.currentTimeMillis())}'
         |)""".stripMargin
    try {
      statement.execute(sql)
    } catch {
      case e: DerbySQLIntegrityConstraintViolationException =>
        if (e.getErrorCode != 30000)
          log.error(e.getMessage)
        else notExists = false
      case e1: Exception =>
        log.error(e1.getMessage)
    }
    conn.close()
    notExists
  }

  /*

  table for all files
  update: a,b,c,d,e -> db

  download max 2
  downloaded: a,b
  downlaoded: c,d

  mod1 needs a,c,e
  query job tab: a,b,c,d

  mod2 needs d,c
  query job tab: c,d

   */

  override def getDatabusFileById(id: String): Option[DatabusFile] = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val query = s"SELECT * FROM $databusFileTableName WHERE id = '$id'"
    val databusFile = new DatabusFileIterator(statement.executeQuery(query)).toList.headOption
    conn.close()
    databusFile
  }

  override def databusFilesByModNameAndStatus(modName: String, status: JobStatus.Value): List[DatabusFile] = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val modTable = modName
    val query = s"SELECT databusFiles.* FROM $databusFileTableName, $modTable WHERE $modTable.id = databusFiles.id AND $modTable.status = ${status.id}"
    val databusFiles = new DatabusFileIterator(statement.executeQuery(query)).toList
    conn.close()
    databusFiles
  }

  override def addJob(modName: String, id: String, status: JobStatus.Value = JobStatus.OPEN): Boolean = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    var notExists = true
    val sql =
      s"""
         |INSERT INTO ${modName} VALUES (
         |'$id',
         |${status.id},
         |'${new Timestamp(System.currentTimeMillis())}'
         |)""".stripMargin
    try {
      statement.execute(sql)
    } catch {
      case e: DerbySQLIntegrityConstraintViolationException =>
        if (e.getErrorCode != 30000)
          log.error(e.getMessage)
        else notExists = false
      case e1: Exception =>
        log.error(e1.getMessage)
    }
    conn.close()
    notExists
  }

  override def updateJobStatus(modName: String, id: String, status: JobStatus.Value): Unit = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val sql =
      s"""
         |UPDATE ${modName}
         |SET status = ${status.id}, timestamp = '${new Timestamp(System.currentTimeMillis())}'
         |WHERE id = '$id'""".stripMargin
    try {
      statement.executeUpdate(sql)
    } catch {
      case e: Exception =>
        log.error(e.getMessage)
    }
    conn.close()
  }

  override def checkOverallStatus(id: String, modNames: Array[String]): Array[Int] = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val buffer = new ArrayBuffer[Int]
    val sql = modNames.map(modName => s"SELECT status FROM $modName WHERE id = '$id'").mkString(" UNION ")
    try {
      val qs = statement.executeQuery(sql)
      while(qs.next()) {
        buffer.append(qs.getInt("status"))
      }
    } catch {
      case e: Exception =>
        log.error(e.getMessage)
    }
    conn.close()
    buffer.toArray
  }

  /**
   * combine to one query
   * @param id
   * @param status
   */
  override def updateDatabusFileStatus(id: String, status: DatabusFileStatus): Unit = {

    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val sql =
      s"""
         |UPDATE $databusFileTableName
         |SET status = ${status.id}, timestamp = '${new Timestamp(System.currentTimeMillis())}'
         |WHERE id = '$id'""".stripMargin
    try {
      statement.executeUpdate(sql)
    } catch {
      case e: Exception =>
        log.error(e.getMessage)
    }
    conn.close()
  }
}
