package org.dbpedia.databus_mods.lib.worker.base

import java.io.FileOutputStream
import java.time.{Duration, Instant}
import java.util.Calendar

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.{Component, Service}

@Component
class AsyncExecutor(queue: PeekLBQueue, process: Process, repository: FileRepository) extends CommandLineRunner {

  private val log = LoggerFactory.getLogger(classOf[AsyncExecutor])

  override def run(args: String*): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        while (true) {
          val task = queue.peek()
          log.info(s"start process ${task.dataIdFilePath} with ${task.source}")
          val time = Instant.now()
          try {
            val dataIDExtension = new DataIDExtension(repository,task.dataIdFilePath,task.source)
            dataIDExtension.setStart(Calendar.getInstance())
            process.run(dataIDExtension)
            dataIDExtension.setEnd(Calendar.getInstance())
            dataIDExtension.getModel.write(new FileOutputStream(repository.createFile(task.dataIdFilePath)),"TURTLE")
          } catch {
            case e: Exception =>
              println(
                """#####################
                  | TODO catch properly
                  |#####################
                  |""".stripMargin)
              e.printStackTrace()
          }
          queue.take()
          log.info(s"done process ${task.dataIdFilePath} with ${task.source} took ${Duration.between(time, Instant.now())}ms")
        }
      }
    }).start()
  }
}
