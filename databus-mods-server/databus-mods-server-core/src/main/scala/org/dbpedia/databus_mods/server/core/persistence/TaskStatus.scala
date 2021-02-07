package org.dbpedia.databus_mods.server.core.persistence

object TaskStatus extends Enumeration {
  val Open,Wait,Done,Fail = Value
}
