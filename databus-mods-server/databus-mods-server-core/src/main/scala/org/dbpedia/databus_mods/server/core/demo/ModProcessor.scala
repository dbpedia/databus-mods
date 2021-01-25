package org.dbpedia.databus_mods.server.core.demo

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference

import org.dbpedia.databus_mods.server.core.Config
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.{Component, Service}

@Service
class ModProcessor(modRepo: ModRepo)
  extends CommandLineRunner {

  private val modQueue = new LinkedBlockingQueue[String]()

  private val current = new AtomicReference[String]()

  def addProcess(simpleId: String): Boolean = {
    if (modQueue.contains() || (current.get() == simpleId)) {
      false
    } else {
      modQueue.put(simpleId)
      true
    }
  }

  override def run(args: String*): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        while (true) {
          val simpleId = modQueue.take()
          current.set(simpleId)
          modRepo.cacheLoader.refresh(simpleId)
          current.set(null)
        }
      }
    }).start()
  }
}
