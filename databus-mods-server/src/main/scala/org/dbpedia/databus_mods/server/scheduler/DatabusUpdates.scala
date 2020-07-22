package org.dbpedia.databus_mods.server.scheduler

import org.apache.jena.query.QueryExecutionFactory
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory}
import org.dbpedia.databus_mods.server.{Config, ModConfig}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

@Component
@Scheduled
class DatabusUpdates @Autowired()(config: Config) {

  private val dbConnection = DbFactory.derbyDb(
    config.database.databaseUrl,
    config.getMods.asScala.map(_.name).toList
  )

  private val log = LoggerFactory.getLogger(classOf[DatabusUpdates])

  @Scheduled(fixedRate = 15 * 60 * 1000)
  def cronjob(): Unit = {

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
      responseSize = 0
      val sparql = query + s" LIMIT $limit OFFSET $offset"
      val queryExec = QueryExecutionFactory.sparqlService(
        "https://databus.dbpedia.org/repo/sparql", sparql
      )
      val resultSet = queryExec.execSelect()
      while (resultSet.hasNext) {
        responseSize += 1
        val qs = resultSet.next()
        val databusFile = DatabusFile(
          qs.getResource("file").getURI,
          qs.getLiteral("sha256sum").getLexicalForm,
          qs.getResource("downloadURL").getURI
        )
        if (dbConnection.insertDatabusFile(databusFile))
          addedDatabusFiles += 1
        if (dbConnection.addJob(modName, databusFile.id))
          addedJobs += 1
      }
      offset += limit
      queryExec.close()
    } while (responseSize != 0)
    log.info(s"Mod '$modName' - added $addedDatabusFiles DatabusFiles & added $addedJobs Jobs")
  }
}