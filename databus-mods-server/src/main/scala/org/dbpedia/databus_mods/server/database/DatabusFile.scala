package org.dbpedia.databus_mods.server.database


object DatabusFile {

  def apply(dataidFile: String,
            sha256sum: String,
            downloadUrl: String): DatabusFile = {

    val List(publisher, group, artifact, version, fileName) =
      dataidFile.split("/").reverseIterator.take(5).toList

    val id: String = Array(publisher, group, artifact, version, fileName).mkString("/")

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

case class  DatabusFile
(
  id: String,
  publisher: String,
  group: String,
  artifact: String,
  version: String,
  fileName: String,
  sha256sum: String,
  downloadUrl: String
)
