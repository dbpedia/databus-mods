package org.dbpedia.databus_mods.server

object DatabusFileStatus extends Enumeration {
  type DatabusFileStatus = Value
  val WAIT,ACTIVE,DONE,FAILED = Value
}