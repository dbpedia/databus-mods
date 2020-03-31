package org.dbpedia.databus.indexer

import java.net.URL
import java.sql.ResultSet

/**
 * decorating resultset
 *
 * @param rs
 */
class ItemSet(val rs: ResultSet) {

  def next: Boolean = {
    rs.next()
  }

  def getItem: Item = {

    new Item(
      rs.getString("shasum"),
      new URL(rs.getString("downloadURL")),
      new URL(rs.getString("dataset")),
      new URL(rs.getString("version")),
      new URL(rs.getString("distribution"))
    )
  }

  def close: Unit = {
    rs.close()
  }

}
