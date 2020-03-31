package org.dbpedia.databus.indexer

import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

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

  def setStatusProcessed(shasum: String) = {
    val conn = DriverManager.getConnection(databaseURL)
    val statement = conn.createStatement
    val sql =
      s"""
         |UPDATE item SET status = 'processed'
         |WHERE shasum = '${shasum}'
         |""".stripMargin
    try {
      statement.executeUpdate(sql)
    } catch {
      case e: Exception => e.printStackTrace()
      //case e: DerbySQLIntegrityConstraintViolationException => println("IGNORE: " + e.getMessage) //TODO do nothing
    }
    conn.close()
  }

  def printNewResultSets = {
    val rs = getNewResultSet
    while (rs.next) {
      val item = rs.getItem
      System.out.println(item)
    }
    rs.close
  }

  /**
   * retrieves all with status open
   *
   * @return ResultSet for iterating into threads
   */
  def getNewResultSet: ItemSet = {

    val conn = DriverManager.getConnection(databaseURL)
    val statement = conn.createStatement
    val query = "SELECT * FROM item WHERE status = 'open'"
    val rs: ResultSet = statement.executeQuery(query)
    new ItemSet(rs)
  }

}
