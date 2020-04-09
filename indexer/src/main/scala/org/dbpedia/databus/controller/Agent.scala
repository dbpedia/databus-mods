package org.dbpedia.databus.controller

import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.process.Processor
import org.dbpedia.databus.sink.Sink

class Agent ( val processors: java.util.List[Processor], val sink:Sink ) {


  def process(item:Item) = {

    // download / check if downloaded

    // process and sink
    processors.forEach(_.process(item,sink))


  }

}
