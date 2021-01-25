package org.dbpedia.databus_mods.server.database

import org.dbpedia.databus_mods.server.database.derby.DerbyDbHandlerImpl

object DbFactory {

  def derbyDb(databaseUrl: String, modNames: List[String]): AbstractDbHandler = {
    val db = new DerbyDbHandlerImpl(databaseUrl)
    db.createDatabusFilesTable()
    modNames.foreach(modName => db.createModProcessTable(modName))
    db
  }
}
