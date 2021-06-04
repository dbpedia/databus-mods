package org.dbpedia.databus_mods.lib.worker.execution

import org.dbpedia.databus_mods.lib.model.MetadataExtensionBuilder

trait ModProcessor {

  def process(extension: Extension)
}
