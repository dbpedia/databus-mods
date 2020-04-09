package org.dbpedia.databus.sink

class PrintSink extends Sink {



  def consume(output:String) = {
    println(output)
  }

}
