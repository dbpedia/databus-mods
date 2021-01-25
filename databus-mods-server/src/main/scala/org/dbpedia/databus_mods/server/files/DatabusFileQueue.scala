package org.dbpedia.databus_mods.server.files

import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicInteger

class DatabusFileQueue[E] extends LinkedBlockingDeque[E] {

  private val allowedTakes = new AtomicInteger(20)
  private val currentTakes = new AtomicInteger(0)

  override def add(e: E): Boolean = {
    super.add(e)
  }

  override def take(): E = {
    super.take()
  }
}
