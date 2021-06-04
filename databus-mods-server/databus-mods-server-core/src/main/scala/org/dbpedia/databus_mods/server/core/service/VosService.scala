package org.dbpedia.databus_mods.server.core.service

import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import virtuoso.jena.driver.VirtDataset

@Service
class VosService(
                  @Value("${mod-server.triple-store.db-url}") url: String,
                  @Value("${mod-server.triple-store.db-user}") usr: String,
                  @Value("${mod-server.triple-store.db-password}") psw: String) {

  private val log = LoggerFactory.getLogger(classOf[VosService])

  def addNamedModel(
                     name: String,
                     model: Model,
                     replace: Boolean = false)
  : Unit = synchronized {
    val vds = new VirtDataset(url, usr, psw)
    if (replace) vds.replaceNamedModel(name, model)
    else vds.addNamedModel(name, model, false)
    vds.commit()
    vds.close()
    log.info(s"loaded $name")
  }
}

object VosService extends App {

}
