package org.dbpedia.databus_mods.server.database

import java.sql.{Date, DriverManager, ResultSet, SQLException, Timestamp}

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException
import org.slf4j.LoggerFactory

class DerbyDbHandlerImpl(databaseUrl: String) extends AbstractDbHandler {

  private val log = LoggerFactory.getLogger(classOf[DerbyDbHandlerImpl])

  def createDatabusFilesTable(): Unit = {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    try {
      val sql: String =
        """CREATE TABLE databusFiles (
          |id          varchar(2000) primary key,
          |publisher   varchar(2000),
          |grp         varchar(2000),
          |artifact    varchar(2000),
          |version     varchar(2000),
          |fileName    varchar(2000),
          |sha256sum   varchar(128),
          |downloadUrl varchar(2000)
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
         |INSERT INTO databusFiles VALUES (
         |'${databusFile.id}',
         |'${databusFile.publisher}',
         |'${databusFile.group}',
         |'${databusFile.artifact}',
         |'${databusFile.version}',
         |'${databusFile.fileName}',
         |'${databusFile.sha256sum}',
         |'${databusFile.downloadUrl}'
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

  override def getDatabusFileById(id: String): Option[DatabusFile] = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val query = s"SELECT * FROM databusFiles WHERE id = '$id'"
    val databusFile = new DatabusFileIterator(statement.executeQuery(query)).toList.headOption
    conn.close()
    databusFile
  }

  override def databusFilesByModNameAndStatus(modName: String, status: JobStatus.Value): List[DatabusFile] = {
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    val modTable = modName
    val query = s"SELECT databusFiles.* FROM databusFiles, $modTable WHERE $modTable.id = databusFiles.id AND $modTable.status = ${status.id}"
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
         |SET status = ${status.id}
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
