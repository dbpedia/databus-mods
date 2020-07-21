package org.dbpedia.databus_mods.server.database

abstract class DbHandler {

  def tableNameHash(name: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    new sun.misc.BASE64Encoder().encode(md.digest(name.getBytes))
  }

  def createDatabusFilesTable(): Unit

  def createModProcessTable(name: String): Unit
}
