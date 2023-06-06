package org.dbpedia.databus.mods.core.model

import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.MetadataType

import java.net.URI

/**
 * Data artifact created by a mod activity
 *
 * @param used the data artifact used as input
 * @param id
 * @param metadataType
 */
class ModResult(
  val id: URI,
  val suffix: String,
  val used: URI,
  val metadataType: MetadataType
)

