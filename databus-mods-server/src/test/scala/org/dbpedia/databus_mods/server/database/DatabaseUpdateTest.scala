//package org.dbpedia.databus_mods.server.database
//
//import java.io.File
//
//import org.apache.commons.io.FileUtils
//import org.junit.jupiter.api.Test
//import org.slf4j.{Logger, LoggerFactory}
//
//
//class DatabaseUpdateTest {
//
//  private val log: Logger = LoggerFactory.getLogger(classOf[DatabaseUpdateTest])
//  private val databaseUrl = "jdbc:derby:./target/testDB"
//  FileUtils.deleteDirectory(new File("./target/testDB"))
//  private val db = DbFactory.derbyDb(databaseUrl,List("demo"))
//
//  private val queryString =
//    """PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
//      |PREFIX dct:    <http://purl.org/dc/terms/>
//      |PREFIX dcat:   <http://www.w3.org/ns/dcat#>
//      |PREFIX db:     <https://databus.dbpedia.org/>
//      |PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
//      |PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#>
//      |
//      |SELECT DISTINCT ?file ?sha256sum ?downloadURL WHERE {
//      |  ?s a dataid:Dataset.
//      |  ?s dataid:group <https://databus.dbpedia.org/vehnem/animals> .
//      |  ?s dcat:distribution ?distribution .
//      |  ?distribution dataid:file ?file .
//      |  ?distribution dataid:sha256sum ?sha256sum .
//      |  ?distribution dcat:downloadURL ?downloadURL .
//      |}
//      |""".stripMargin
//
//  @Test
//  def updateTest(): Unit = {
//
//    DatabusQueryUtil.getUpdates(queryString).foreach( databusFile => {
////      db.insertDatabusFile(databusFile)
//    })
//  }
//}
