package org.dbpedia.databus.mods.core.model

import org.dbpedia.databus.dataid.Part
import org.dbpedia.databus.mods.core.MetadataType

import java.net.URI

object ModResultFactory {

  def enrichment(used: URI): ModResult = {
    new ModResult(null, null, used, MetadataType.Enrichment)
  }

  def statistics(used: URI): ModResult = {
    new ModResult(null, null, used,MetadataType.Statistics)
  }

  def summary(used: URI): ModResult = {
    new ModResult(null, null, used,MetadataType.Summary)
  }
}
