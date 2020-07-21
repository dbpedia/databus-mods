package org.dbpedia.databus_mods.server.database

object ProcessStatus extends Enumeration {
  type ProcessStatus = Value
  val OPEN,ACTIVE,DONE,FAILED = Value
}
