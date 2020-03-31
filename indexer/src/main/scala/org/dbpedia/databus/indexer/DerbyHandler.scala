package org.dbpedia.databus.indexer

import java.net.URL
import java.sql.DriverManager
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException

object DerbyHandler {

  val databaseURL = "jdbc:derby:.indexdb;create=true"
  val wtf = init()

  def init(): Boolean = {

    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection(databaseURL)
    val statement = conn.createStatement
    try {
      val sql: String =
        """CREATE TABLE item (
          |shasum varchar(128) primary key,
          |status varchar(20),
          |downloadURL varchar(2000),
          |dataset varchar(2000),
          |version varchar(2000),
          |distribution varchar(2000)
          |)""".stripMargin
      statement.execute(sql)
    } catch {
      case e: SQLException => println("IGNORE: " + e.getMessage)
    }
    statement.execute("CREATE INDEX status on item (status)")

  }

  def addIfNotExists(shaSum: String,
                     downloadURL: String,
                     dataset: String,
                     version: String,
                     distribution: String) = {
    val conn = DriverManager.getConnection(databaseURL)
    val statement = conn.createStatement
    val sql =
      s"""
         |INSERT INTO item VALUES
         |('${shaSum}','open','${downloadURL}','${dataset}','${version}','${distribution}')
         |""".stripMargin
    try {
      statement.execute(sql)
    } catch {
      case e: DerbySQLIntegrityConstraintViolationException => println("IGNORE: " + e.getMessage) //TODO do nothing
    }
    conn.close()
  }

  def setStatus(shasum: String) = {

  }

  def getNewResultSet: ResultSet = {

    val conn = DriverManager.getConnection(databaseURL)
    val statement = conn.createStatement
    val query = "SELECT * FROM item"
    val rs: ResultSet = statement.executeQuery(query)
    rs

    /* while ( {
       rs.next
     }) {
       val id = rs.getString("shasum")
       val status = rs.getString("status")
       System.out.println(id + " " + status)
     }*/

  }

}
