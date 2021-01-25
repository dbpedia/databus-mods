package org.dbpedia.databus_mods.lib.worker.base

import java.util.concurrent.LinkedBlockingQueue

import org.springframework.stereotype.Component

@Component
class PeekLBQueue extends LinkedBlockingQueue[WorkerTask] {

  override def put(e: WorkerTask): Unit = synchronized {
    super.put(e)
    notify()
  }

  override def peek(): WorkerTask = synchronized {
    if(isEmpty) wait()
    super.peek()
  }
}