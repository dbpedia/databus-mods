package org.dbpedia.databus_mods.lib

import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}

abstract class AbstractDatabusModExecutor extends Runnable {

  override def run(): Unit = {

    while (true) {
      val databusModInput = DatabusModInputQueue.take()
      process(databusModInput)
      DatabusModInputQueue.removeCurrent(databusModInput.id)
    }
  }

  def process(input: DatabusModInput)

}
