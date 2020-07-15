package org.dbpedia.databus_mods.lib

import java.util.concurrent

import scala.collection.immutable.HashMap

object DatabusModInputQueue {

  private val q = new concurrent.LinkedBlockingQueue[DatabusModInput]()

  private val current = new concurrent.ConcurrentHashMap[String, DatabusModInput]()

  def take(): DatabusModInput = {
    val databusModInput = q.take()
    current.put(databusModInput.id, databusModInput)
    databusModInput
  }

  def put(task: DatabusModInput): Unit = {
    q.put(task)
  }

  def removeCurrent(id: String): Unit = {
    current.remove(id)
  }

  def getCurrent: HashMap[String, DatabusModInput] = {
    import scala.collection.JavaConversions._
    HashMap(current.toList: _*)
  }
}