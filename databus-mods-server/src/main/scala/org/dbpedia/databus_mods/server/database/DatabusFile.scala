package org.dbpedia.databus_mods.server.database

import java.sql.ResultSet

import org.apache.jena.riot.RDFDataMgr

object DatabusFile {

  def fromResultSet(rs: ResultSet): DatabusFile = {
    DatabusFile(
      rs.getString("id"),
      rs.getString("publisher"),
      rs.getString("grp"),
      rs.getString("artifact"),
      rs.getString("version"),
      rs.getString("fileName"),
      rs.getString("sha256sum"),
      rs.getString("downloadUrl")
    )
  }

  def apply(dataidFile: String,
            sha256sum: String,
            downloadUrl: String): DatabusFile = {

    val List(publisher, group, artifact, version, fileName) =
      dataidFile.split("/").reverseIterator.take(5).toList.reverse

    val id: String = Array(publisher, group, artifact, version, fileName, sha256sum).mkString("/")

    new DatabusFile(
      id,
      publisher,
      group,
      artifact,
      version,
      fileName,
      sha256sum,
      downloadUrl
    )
  }
}

case class DatabusFile
(
  id: String,
  publisher: String,
  group: String,
  artifact: String,
  version: String,
  fileName: String,
  sha256sum: String,
  downloadUrl: String
) {
  override def toString: String =
    s"""
       |@id         : $id
       |publiher    : $publisher
       |group       : $group
       |artifact    : $artifact
       |version     : $version
       |fileName    : $fileName
       |sha256sum   : $sha256sum
       |downloadUrl : $downloadUrl""".stripMargin
}
