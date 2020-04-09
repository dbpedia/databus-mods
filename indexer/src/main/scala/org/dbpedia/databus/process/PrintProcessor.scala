package org.dbpedia.databus.process

import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

class PrintProcessor extends Processor {

  def process(item:Item, sink:Sink)={

    //example
    sink.consume(item.shaSum)
  }

}
