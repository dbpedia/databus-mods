package org.dbpedia.databus_mods.server.core.execution

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingDeque}

import org.dbpedia.databus_mods.server.core.persistence.Task
import org.springframework.stereotype.Component

@Component
class TaskQueues extends ConcurrentHashMap[String, LinkedBlockingDeque[Task]] {

  def getOrCreate(
                   key: String,
                   q: LinkedBlockingDeque[Task] = new LinkedBlockingDeque[Task]()): LinkedBlockingDeque[Task] = {
    if (this.containsKey(key)) {
      this.get(key)
    } else {
      this.put(key, q)
      q
    }
  }
}
