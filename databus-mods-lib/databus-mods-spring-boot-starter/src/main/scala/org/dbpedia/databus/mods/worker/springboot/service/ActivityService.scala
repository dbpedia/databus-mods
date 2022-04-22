package org.dbpedia.databus.mods.worker.springboot.service

import org.dbpedia.databus.mods.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent._

@Service
class ActivityService() {

  @Autowired
  private var modActivity: ModActivity = _

  private val executor = new ThreadPoolExecutor(
    1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable]())

  private val activities = new ConcurrentHashMap[String, Future[ModActivityMetadata]]()

  // GET Request current
  // TODO implement
  def get(id: String): Option[Future[ModActivityMetadata]] = synchronized {
    val a = activities.get(id)
    println(a)
    a match {
      case null =>
        None
      case futureActivityMetadata: Future[ModActivityMetadata] =>
        if (futureActivityMetadata.isDone) activities.remove(id)
        Some(futureActivityMetadata)
    }
  }

  def submit(activityRequest: ModActivityRequest): Unit = synchronized {
    val future: Future[ModActivityMetadata] = executor.submit(
      new ActivityRunner(activityRequest, modActivity)
    )
    // TODO better put?
    activities.put(activityRequest.id, future)
  }
}
