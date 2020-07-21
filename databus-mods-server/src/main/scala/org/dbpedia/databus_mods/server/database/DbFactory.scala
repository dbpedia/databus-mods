package org.dbpedia.databus_mods.server.database

object DbFactory {

  def derbyDb(derbyFile: String): DbHandler = {
    val db = new DerbyDbHandler(s"""jdbc:derby:${derbyFile}""")
    db.init()
    db
  }
}
