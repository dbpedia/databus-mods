package org.dbpedia.databus.mods.worker.springboot.worker.execution

trait ModProcessor {

  def process(extension: Extension)
}
