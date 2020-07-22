package org.dbpedia.databus_mods.uri_analyzor

import java.util.Calendar

import org.dbpedia.databus_mods.lib.{AbstractDatabusModExecutor, DatabusModInput}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DatabusModExecutor @Autowired()(config: Config) extends AbstractDatabusModExecutor {

  private val log = LoggerFactory.getLogger(classOf[DatabusModExecutor])
  private implicit val basePath: String = config.volumes.localRepo
  def process(databusModInput: DatabusModInput): Unit = {
    try {
      log.info(s"process ${databusModInput.id}")
      val metadataFile = databusModInput.modMetadataFile
      metadataFile.parent.createDirectories()
      // TODO your code
      throw new Exception("evil exception")
    } catch {
      case e: Exception =>
        log.error(s"failed to process ${databusModInput.id}")
       print( databusModInput.modErrorFile.write(
          Calendar.getInstance().getTime.toString + "\n" +
            e.getStackTrace.mkString("\n")
        ).pathAsString)
    }
  }
}
