package org.dbpedia.databus.mods.core.worker.execution

trait ModProcessor {

  def process(extension: Extension): Unit
}
