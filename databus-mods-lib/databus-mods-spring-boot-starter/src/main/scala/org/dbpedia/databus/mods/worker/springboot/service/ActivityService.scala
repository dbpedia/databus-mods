package org.dbpedia.databus.mods.worker.springboot.service

import java.util.concurrent._

import org.dbpedia.databus.dataid.SingleFile
import org.dbpedia.databus.mods.model.ModActivityMetadata
import org.springframework.stereotype.Service

@Service
class ActivityService() {

  private val executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable]())

  private val activities = new ConcurrentHashMap[String, Future[ModActivityMetadata]]()

  // GET Request current
  // TODO implement
  def get(dbusSF: SingleFile): Option[Future[ModActivityMetadata]] = synchronized {
    activities.get(dbusSF.path) match {
      case null =>
        None
      case mam =>
        if(mam.isDone) activities.remove(dbusSF.path)
        Some(mam)
    }
  }

  def submit(activityPlan: ActivityPlan): Unit = synchronized {
    val future: java.util.concurrent.Future[ModActivityMetadata] = executor.submit(activityPlan)
    activities.put(activityPlan.dbusSF.path, future)
  }

  // TODO putOrGet
}
