package org.dbpedia.databus_mods.server.database

import java.sql.ResultSet

import org.slf4j.LoggerFactory

import scala.collection.AbstractIterator

class DatabusFileIterator(val rs: ResultSet) extends AbstractIterator[DatabusFile] {

  private var didNext : Boolean = false;
  private var _hasNext : Boolean = false;

  def next(): DatabusFile = {
    if (!didNext) {
      rs.next()
    }
    didNext = false;
    DatabusFile.fromResultSet(rs)
  }

  def hasNext(): Boolean = {
    if (!didNext) {
      _hasNext = rs.next();
      didNext = true;
    }
    _hasNext;
  }
}
