package org.dbpedia.databus_mods.void.gtd

import java.io.FileOutputStream
import java.time.{Duration, Instant}
import java.util.Calendar

import org.dbpedia.databus_mods.void.Config
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class Executor(queue: Queue) extends CommandLineRunner {

  @Autowired var config: Config = _

  private val log: Logger = LoggerFactory.getLogger(classOf[Executor])

  @Autowired var repo: Repo = _

  override def run(args: String*): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = {
        while (true) {
          val task = queue.peek()
          log.info(s"start process ${task.dataIdFilePath} with ${task.source}")
          val time = Instant.now()
          process(task)
          queue.take()
          log.info(s"done process ${task.dataIdFilePath} with ${task.source} took ${Duration.between(time, Instant.now())}ms")
        }
      }
    }).start()
  }

  def process(task: Task): Unit = {

    val startTime = Calendar.getInstance()
    val inputStream = UriUtil.openStream(task.source)
    val pipedRDF = IORdfUtil.toPipedRDF(inputStream)

    try {
      if (pipedRDF.hasNext) {
        val (classPartitionMap, propertyPartitionMap) = VoIDUtil.calculateVoIDPartitions(pipedRDF)
        val voidModel = VoIDUtil.toJenaModel(classPartitionMap, propertyPartitionMap)
        voidModel.setNsPrefix("void", "http://rdfs.org/ns/void#")

        val voidFileName = "rdfVoid.ttl"
        val voidFile = repo.createFile(task.dataIdFilePath, voidFileName)
        voidModel.write(new FileOutputStream(voidFile), "TURTLE")

        val metadataModel = new DatabusMod(
          task.dataIdFilePath,
          List(voidFileName),
          startTime,
          Calendar.getInstance).getModel
        val metadataFile = repo.createFile(task.dataIdFilePath)
        metadataModel.write(new FileOutputStream(metadataFile), "TURTLE")
      } else {
        log.warn(s"empty iterator ${task.dataIdFilePath} with ${task.source}")
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
