package org.dbpedia.databus_mods.server.database

import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory

class DbFactoryTest extends AnyFunSuite {

  private val log = LoggerFactory.getLogger(classOf[DbFactoryTest])

  test("Derby DB Test") {

    val modName = "modDummy"

    val db = DbFactory.derbyDb("jdbc:derby:./.derby", List(modName))

    val databusFile =
      DatabusFile(
        "https://databus.dbpedia.org/dbpedia/generic/labels/2020.06.01/labels_lang=de.ttl.bz2",
        "2c1eee623772e00d9cd09fcea19eeee74376437b8ebbc9b372b4508b2c431d0e",
        "https://downloads.dbpedia.org/repo/dbpedia/generic/labels/2020.06.01/labels_lang=de.ttl.bz2"
      )

    db.insertDatabusFile(databusFile)

    db.getDatabusFileById(databusFile.id) match {
      case Some(databusFile) => log.info(databusFile.toString)
      case None => log.error(s"not found: ${databusFile.id}")
    }

    log.info("1")
    db.createModProcessTable(modName)

    log.info("2")
    db.addJob(modName, databusFile.id, JobStatus.OPEN)
    db.databusFilesByModNameAndStatus(modName, JobStatus.OPEN).foreach(
      x => log.info("Open jobs: " + x)
    )

    log.info("3")
    db.updateJobStatus(modName, databusFile.id, JobStatus.ACTIVE)
    db.databusFilesByModNameAndStatus(modName, JobStatus.ACTIVE).foreach(
      x => log.info("Active jobs: " + x)
    )
  }
}
