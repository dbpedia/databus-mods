package org.dbpedia.databus.mods.core.worker.exec

import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityRequest}

import java.util.concurrent.{ConcurrentHashMap, Future, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}

// https://stackoverflow.com/questions/8905780/thread-pool-handling-duplicate-tasks
class ActivityExecution(modActivity: ModActivity) {

  private val executor = new ThreadPoolExecutor(
    1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable]())

  private val activities = new ConcurrentHashMap[String, Future[ModActivityMetadata]]()

  // GET Request current
  // TODO implement
  def get(id: String): Option[Future[ModActivityMetadata]] = synchronized {
      activities.get(id) match {
      case null =>
        None
      case futureActivityMetadata: Future[ModActivityMetadata] =>
        if (futureActivityMetadata.isDone) activities.remove(id)
        Some(futureActivityMetadata)
    }
  }

  def submit(activityRequest: ModActivityRequest): Unit = synchronized {
    val future: Future[ModActivityMetadata] = executor.submit(
      new ActivityTask(activityRequest, modActivity)
    )
    // TODO better put?
    activities.put(activityRequest.dataId, future)
  }
}
