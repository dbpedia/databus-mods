package org.dbpedia.databus_mods.lib.worker.execution

import java.io.{FileOutputStream, PrintWriter}
import java.time.{Duration, Instant}
import java.util.Calendar

import org.dbpedia.databus_mods.lib.worker.service.FileService
import org.slf4j.LoggerFactory

class ModExecutor(processor: ModProcessor, queue: ModQueue[ModRequest], fileService: FileService) extends Runnable {

  private val log = LoggerFactory.getLogger(classOf[ModExecutor])

  override def run(): Unit = {
    while (true) {
      val request = queue.cachedTake()
      log.info(s"start process ${request.databusID} with ${request.sourceURI}")
      val time = Instant.now()
      try {
        val dataIDExtension = new Extension(fileService, request.databusPath, request.sourceURI)
        dataIDExtension.setStart(Calendar.getInstance())
        processor.process(dataIDExtension)
        dataIDExtension.setEnd(Calendar.getInstance())
        dataIDExtension.getModel.write(new FileOutputStream(fileService.createFile(request.databusPath)), "TURTLE")
      } catch {
        case e: Exception =>
          val errorFile = fileService.createFile(request.databusPath, "error.log")
          e.printStackTrace()
          e.printStackTrace(new PrintWriter(errorFile))
      }
      queue.removeFromCache(request)
      log.info(s"processed ${request.databusPath} with ${request.sourceURI} took ${Duration.between(time, Instant.now())}ms")
    }
  }
}
