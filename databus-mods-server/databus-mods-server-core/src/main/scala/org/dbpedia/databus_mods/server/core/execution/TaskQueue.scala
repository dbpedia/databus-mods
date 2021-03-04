package org.dbpedia.databus_mods.server.core.execution

import java.util.concurrent.{LinkedBlockingDeque}

import org.dbpedia.databus_mods.server.core.persistence.Task

class TaskQueue {

  // every access is synced now, so could be replaced by normal Dequeue
  private val queue = new LinkedBlockingDeque[Task]()

  def putIfAbsent(task: Task, moveToFirst: Boolean = false): Unit = synchronized {
    if(contains(task) && moveToFirst) {
      remove(task)
      queue.putFirst(task)
    } else if(moveToFirst) {
      queue.putFirst(task)
    } else if(! contains(task)) {
      queue.putLast(task)
    }
  }

  def take(): Task =  {
    queue.take()
  }

  def remove(task: Task): Unit =  {
    queue.remove(task)
  }

  def contains(task: Task): Boolean = {
    queue.contains(task)
  }
}
