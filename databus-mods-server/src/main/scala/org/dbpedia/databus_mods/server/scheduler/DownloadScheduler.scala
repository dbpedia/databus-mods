package org.dbpedia.databus_mods.server.scheduler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Scheduled
class DownloadScheduler {

  @Scheduled(fixedDelay = 30 * 10000)
  def a(): Unit = {
    // TODO put into DownloadQueue and respect needs of mods
  }
}
