package org.dbpedia.databus.sink

import org.apache.jena.atlas.lib.ProgressMonitor.Output

abstract class Sink {

  def consume(output: String)

}
