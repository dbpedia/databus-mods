package org.dbpedia.databus.mods.worker.springboot.service

import org.dbpedia.databus.dataid.SingleFile
import org.dbpedia.databus.mods.model.ModActivityMetadata
import org.graalvm.compiler.lir.CompositeValue.Component


trait ModActivity {

  def process(modActivityMetadata: ModActivityMetadata)
}
