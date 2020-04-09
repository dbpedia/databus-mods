package org.dbpedia.databus.process

import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

abstract class Processor {

  def process(item:Item, sink:Sink)
}
