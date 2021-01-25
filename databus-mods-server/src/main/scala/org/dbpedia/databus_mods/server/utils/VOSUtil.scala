package org.dbpedia.databus_mods.server.utils

import org.apache.jena.query.LabelExistsException
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory

class VOSUtil

object VOSUtil {

  private val log = LoggerFactory.getLogger(classOf[VOSUtil])

  def submitToEndpoint(graphName: String, model: Model, databaseUrl: String, databaseUsr: String, databasePsw: String): Unit = {
    import virtuoso.jena.driver.VirtDataset
    // TODO conf parameter
    val dataSet = new VirtDataset(
      databaseUrl, databaseUsr, databasePsw)
    try {
      dataSet.addNamedModel(graphName, model)
      dataSet.commit()
    } catch {
      case lee: LabelExistsException =>
        // TODO overwrite?
        log.warn(s"Graph exists - $graphName")
      case e: Throwable => e.printStackTrace()
    }

    dataSet.close()
  }
}
