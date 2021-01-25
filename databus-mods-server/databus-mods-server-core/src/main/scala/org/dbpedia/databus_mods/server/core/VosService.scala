package org.dbpedia.databus_mods.server.core

import org.apache.jena.rdf.model.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.{Component, Service}
import virtuoso.jena.driver.VirtDataset

@Service
class VosService(config: Config){

  private val dataset = new VirtDataset(
    config.getProvider.getSparql.getDatabaseUrl,
    config.getProvider.getSparql.getDatabaseUsr,
    config.getProvider.getSparql.getDatabasePsw)

  def addNamedModel(name: String, model: Model, `override`: Boolean = false): Unit = {
    synchronized {
      if(`override`){
        dataset.removeNamedModel(name)
      }
      dataset.addNamedModel(name,model)
      dataset.commit()
      println(s"added $name to ${config.getProvider.getSparql.getDatabaseUrl}")
    }
  }


}
