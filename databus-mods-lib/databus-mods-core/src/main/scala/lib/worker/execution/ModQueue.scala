package lib.worker.execution

import java.util.concurrent.LinkedBlockingQueue

import org.spark_project.jetty.util.ConcurrentHashSet

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

class ModQueue[T] extends LinkedBlockingQueue[T] {

  private val cache = new ConcurrentHashSet[T]

  def removeFromCache(e: T): Unit = {
    cache.remove(e)
  }

  def cachedTake(): T = {
    val item = take()
    cache.add(item)
    item
  }

  def putIfAbsent(e: T): Unit = synchronized {
    if(! contains(e) && ! cache.contains(e))
      put(e)
  }

  def getCacheIterator = {
    cache.iterator()
  }

  def getQueueIterator = {
    this.toIterator
  }
}
