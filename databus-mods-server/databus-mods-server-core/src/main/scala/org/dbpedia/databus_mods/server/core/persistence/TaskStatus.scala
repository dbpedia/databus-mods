package org.dbpedia.databus_mods.server.core.persistence


/**
 * TODO like hadoop TaskStatus
 */
object TaskStatus extends Enumeration {
  val Open,Wait,Done,Fail = Value
}
