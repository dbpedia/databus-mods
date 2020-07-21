package org.dbpedia.databus_mods.server.database

import java.sql.{DriverManager, SQLException}

class DerbyDbHandler(databaseUrl: String) extends DbHandler {

  def init(): Unit = {
    createDatabusFilesTable()
  }

  def createDatabusFilesTable(): Unit = {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    try {
      val sql: String =
        """CREATE TABLE item (
          |fileId varchar(2000) primary key,
          |publisher varchar(2000),
          |group varchar(2000),
          |artifact varchar(2000),
          |version varchar(2000),
          |fileName varchar(2000),
          |sha256sum varchar(128),
          |downloadURL varchar(2000),
          |)""".stripMargin
      statement.execute(sql)
    } catch {
      case e: SQLException => println("IGNORE: " + e.getMessage)
    }
  }

  def createModProcessTable(name: String): Unit = {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseUrl + ";create=true")
    val statement = conn.createStatement
    try {
      val sql: String =
        s"""CREATE TABLE ${tableNameHash(name)} (
           |fileId varchar(2000) primary key,
           |status varchar(20),
           |lastModified varchar(100)
           |)""".stripMargin
      statement.execute(sql)
    } catch {
      case e: SQLException => println("IGNORE: " + e.getMessage)
    }
  }
}
