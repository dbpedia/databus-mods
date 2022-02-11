package org.dbpedia.databus.mods.core.worker.execution

import java.util
import java.util.concurrent.LinkedBlockingQueue
import scala.collection.JavaConversions._

class ModQueue[T] extends LinkedBlockingQueue[T] {

  //TODO needs to be concurrent
  private val cache = new util.HashSet[T]

  def removeFromCache(e: T): Unit = {
    cache.remove(e)
  }

  def cachedTake(): T = {
    val item = take()
    cache.add(item)
    item
  }

  def putIfAbsent(e: T): Unit = synchronized {
    if (!contains(e) && !cache.contains(e))
      put(e)
  }

  def getCacheIterator: util.Iterator[T] = {
    cache.iterator()
  }

  def getQueueIterator: util.Iterator[T] = {
    this.toIterator
  }
}
