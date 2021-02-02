package org.dbpedia.databus_mods.server.web_ui

import org.apache.jena.rdf.model.Model
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import virtuoso.jena.driver.VirtDataset

@Service
class Vos(
           @Value("${tmp.url}") dbUrl : String,
           @Value("${tmp.usr}") dbUsr : String,
           @Value("${tmp.psw}") dbPsw : String) {

  val db = new VirtDataset(dbUrl,dbUsr,dbPsw)

  def loadModel(graphName: String, model:Model, delete: Boolean = false): Unit = {
    if(delete) db.removeNamedModel(graphName)
    db.addNamedModel(graphName,model,false)
    db.commit()
    println(s"vos loaded $graphName")
  }
}
