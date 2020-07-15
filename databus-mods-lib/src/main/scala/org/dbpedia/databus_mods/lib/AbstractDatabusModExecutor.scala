package org.dbpedia.databus_mods.lib

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
