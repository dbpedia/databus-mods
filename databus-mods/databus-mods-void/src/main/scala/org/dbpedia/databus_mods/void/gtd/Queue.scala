package org.dbpedia.databus_mods.void.gtd

import java.util.concurrent.LinkedBlockingQueue

import org.springframework.stereotype.Component

@Component
class Queue extends LinkedBlockingQueue[Task] {

  override def put(e: Task): Unit = synchronized {
    super.put(e)
    notify()
  }

  override def peek(): Task = synchronized {
    if(isEmpty) wait()
    super.peek()
  }
}
