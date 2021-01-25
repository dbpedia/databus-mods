//package org.dbpedia.databus_mods.lib
//
//import java.util.concurrent
//import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}
//
//import scala.collection.immutable.HashMap
//
//class DatabusModInputQueue {
//
//  private val queue = new LinkedBlockingQueue[DatabusModInput]()
//  private val currentTakes = new ConcurrentHashMap[String,DatabusModInput]()
//
//  def take(): DatabusModInput = {
//    val databusModInput = queue.take()
//    currentTakes.put(databusModInput.id, databusModInput)
//    databusModInput
//  }
//
//  def put(task: DatabusModInput): Unit = {
//    queue.put(task)
//  }
//
//  def contains(id: String): Boolean = {
//    // TODO
//    import scala.collection.JavaConversions._
//    (queue.map(_.id).toSet++getCurrent.keySet).contains(id)
//  }
//
//  def removeCurrent(id: String): Unit = {
//    currentTakes.remove(id)
//  }
//
//  def getCurrent: HashMap[String, DatabusModInput] = {
//    import scala.collection.JavaConversions._
//    HashMap(currentTakes.toList: _*)
//  }
//}
