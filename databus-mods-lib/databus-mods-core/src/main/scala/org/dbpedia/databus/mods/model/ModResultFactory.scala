package org.dbpedia.databus.mods.model

import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.MetadataType

object ModResultFactory {

  def enrichment(used: String, name: String): ModResult = {
    new ModResult(Part(used),MetadataType.Enrichment, name)
  }

  def statistics(used: String, name: String): ModResult = {
    new ModResult(Part(used),MetadataType.Statistics, name)
  }

  def summary(used: String, name: String): ModResult = {
    new ModResult(Part(used),MetadataType.Summary, name)
  }
}
