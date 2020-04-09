package org.dbpedia.databus.controller

import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.process.Processor
import org.dbpedia.databus.sink.Sink

class Agent ( val processors: java.util.List[Processor], val sink:Sink ) {


  def process(item:Item) = {

    // download / check if downloaded
    //TODO Fabian databus client
    //add a dir variable and whatever you need.
    //Note that you can also change the variables in Item or tell me to change them in case you need different fields

    // process and sink
    // TODO Fabian
    // Processor.scala needs to extended, as def process method needs more variables, such as the filename, etc. 
    processors.forEach(_.process(item,sink))


  }

}
