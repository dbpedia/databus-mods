package org.dbpedia.databus_mods.server.tasks

import java.util.concurrent.LinkedBlockingQueue

import org.dbpedia.databus_mods.server.ModConfig

class ModService(modConfig: ModConfig, queue: LinkedBlockingQueue[String]) extends Runnable {

  override def run(): Unit = {
    while (true) {
      val job = queue.take()
      println(modConfig.name+" "+job)
    }
  }
}
