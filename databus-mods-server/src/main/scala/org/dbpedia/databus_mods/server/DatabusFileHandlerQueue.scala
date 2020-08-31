package org.dbpedia.databus_mods.server

import java.util.concurrent
import java.util.concurrent.atomic.AtomicInteger

import org.dbpedia.databus_mods.server.database.DatabusFile

object DatabusFileHandlerQueue {

  // redundant to have a LinkedBlockingQueue here?
  private val q = new concurrent.LinkedBlockingQueue[DatabusFile]()

  private val allowedTakes = new AtomicInteger(20)
  private val currentTakes = new AtomicInteger(0)

  def setAllowedTakes(int: Int): Unit = {
    allowedTakes.set(int)
  }

  def decrementCurrentTakes(): Unit = {
    this.synchronized {
      if(currentTakes.get() > 0) {
//        System.err.println("decremented currentTakes")
        currentTakes.decrementAndGet()
      }
      notify
    }
  }

  def take(): DatabusFile = {
    this.synchronized {
//      System.err.println(s"cur: ${currentTakes.get()} all: ${allowedTakes}")
      while(currentTakes.get() >= allowedTakes.get()) {
//        System.err.println("currentTakes > allowedTakes")
        wait
      }
      // TODO check before or after q.take() (because q.take blocks)
      val t=q.take()
      currentTakes.incrementAndGet()
//      System.err.println("taken")
      t
    }
  }

  def put(databusFile: DatabusFile): Unit = {
    q.put(databusFile)
  }
}
