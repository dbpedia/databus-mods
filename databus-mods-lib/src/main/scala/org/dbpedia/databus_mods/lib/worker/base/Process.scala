package org.dbpedia.databus_mods.lib.worker.base

import java.io.OutputStreamWriter

trait Process {

  def run(ext: DataIDExtension): Unit
}
