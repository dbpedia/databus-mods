package org.dbpedia.databus_mods.server.database

object DbFactory {

  def derbyDb(databaseUrl: String, modNames: List[String]): AbstractDbHandler = {
    val db = new DerbyDbHandlerImpl(databaseUrl)
    db.createDatabusFilesTable()
    modNames.foreach(modName => db.createModProcessTable(modName))
    db
  }
}
