package org.dbpedia.databus.mods.model

import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.MetadataType

/**
 * Files created by the mod activity
 *
 * @param metadataType
 * @param property
 */
class ModResult(
  val used: Part,
  val metadataType: MetadataType,
  val name: String
) extends ModMetadata {

}

