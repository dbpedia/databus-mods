package org.dbpedia.databus.mods.core.databus

object DatabusIdentifierType extends Enumeration {
  type DatabusIdentifierType = Value
  val PUBLISHER_ID, GROUP_ID, ARTIFACT_ID, VERSION_ID, FILE_ID = Value
}
