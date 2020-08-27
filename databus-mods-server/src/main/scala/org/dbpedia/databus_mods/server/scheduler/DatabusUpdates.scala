package org.dbpedia.databus_mods.server.scheduler

import org.apache.jena.query.QueryExecutionFactory
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory}
import org.dbpedia.databus_mods.server.{Config, DatabusFileHandlerQueue, ModConfig}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

@Component
@Scheduled
class DatabusUpdates @Autowired()(config: Config) {

  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    config.getMods.asScala.map(_.name).toList
  )

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdates])

    @Scheduled(fixedDelay = 5 * 60 * 1000)
//  @Scheduled(fixedDelay = 10 * 1000)
  def cronjob(): Unit = {
    // TODO parallel or join before insert
    config.mods.asScala.foreach({
      case conf: ModConfig =>
        updateDatabase(conf.name, conf.query)
      case _ => log.warn("incorrect mod config")
    })
  }

  def updateDatabase(modName: String, query: String): Unit = {
    log.info(s"Mod '$modName' - update database")

    var addedDatabusFiles, addedJobs, responseSize, offset = 0
    val limit = 10000
    do {
      log.info(s"${modName} - query offset $offset")
      responseSize = 0
      val sparql = query + s" LIMIT $limit OFFSET $offset"
      val queryExec = QueryExecutionFactory.sparqlService(
        "https://databus.dbpedia.org/repo/sparql", sparql
      )
      val resultSet = queryExec.execSelect()

      val databusFilesBuffer = new ArrayBuffer[DatabusFile]

      while (resultSet.hasNext) {
        responseSize += 1
        val qs = resultSet.next()
        databusFilesBuffer.append(
          DatabusFile(
            qs.getResource("file").getURI,
            qs.getLiteral("sha256sum").getLexicalForm,
            qs.getResource("downloadURL").getURI
          )
        )
      }
      queryExec.close()
      databusFilesBuffer.foreach(databusFile => {
        if (dbConnection.insertDatabusFile(databusFile)) {
          addedDatabusFiles += 1
          // add to download
          DatabusFileHandlerQueue.put(databusFile)
        }
        if (dbConnection.addJob(modName, databusFile.id))
          addedJobs += 1
      })
      offset += limit
    } while (responseSize != 0)
    log.info(s"Mod '$modName' - added $addedDatabusFiles DatabusFiles & added $addedJobs Jobs")
  }
}