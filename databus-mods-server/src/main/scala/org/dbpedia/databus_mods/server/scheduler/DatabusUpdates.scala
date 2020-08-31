package org.dbpedia.databus_mods.server.scheduler

import org.apache.jena.query.QueryExecutionFactory
import org.dbpedia.databus_mods.server.database.{DatabusFile, DbFactory}
import org.dbpedia.databus_mods.server.{Config, DatabusFileHandlerQueue}
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
  def cronjob(): Unit = {

    // TODO parallelize queries

    var addedDatabusFiles, addedJobs = 0
    val updatesBuffer = new ArrayBuffer[(DatabusFile, Array[String])]()

    config.mods.asScala.foreach(mod => {
      val name = mod.name
      val query = mod.query

      val updates = getUpdates(query)
      updates.foreach(databusFile => {
        updatesBuffer.append((databusFile, Array(name)))
      })
    })

    updatesBuffer.groupBy({
      case (databusFile, _) => databusFile.id
    }).map({ group =>
      group._2.reduce(
        (A,B) => (A._1,A._2++B._2)
//        case ((databusFileA, modNamesA), (_, modNamesB)) => (databusFileA, modNamesA ++ modNamesB)
      )
    }).foreach({
      case (databusFile, modNames) =>
        var isOld = false
        if (dbConnection.insertDatabusFile(databusFile)) {
          addedDatabusFiles += 1
          DatabusFileHandlerQueue.put(databusFile)
        } else {
          isOld = true
        }
        modNames.foreach({ modName =>
          if (dbConnection.addJob(modName, databusFile.id)) {
            addedJobs += 1
            // TODO addedJobs per modName
            if (isOld) {
              // TODO update databusFileTable.status
            }
          }
        })
    })
    log.info(s"added $addedDatabusFiles DatabusFiles & added $addedJobs Jobs")
  }

  def getUpdates(query: String): Array[DatabusFile] = {
    val limit = 10000
    var responseSize, offset = 0
    val databusFilesBuffer = new ArrayBuffer[DatabusFile]()

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
        databusFilesBuffer.append(
          DatabusFile(
            qs.getResource("file").getURI,
            qs.getLiteral("sha256sum").getLexicalForm,
            qs.getResource("downloadURL").getURI
          )
        )
      }
      queryExec.close()
      offset += limit
    } while (responseSize != 0)
    databusFilesBuffer.toArray
  }
}