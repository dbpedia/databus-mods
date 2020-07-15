package org.dbpedia.databus_mods.lib

import java.net.URI

import better.files.File

object DatabusModInput {

  def apply
  (publisher: String,
   group: String,
   artifact: String,
   version: String,
   fileName: String,
   fileUri: String
  ): Option[DatabusModInput] = {

    val file = File(new URI(fileUri))
    if (file.exists)

      Some(new DatabusModInput(
        publisher: String,
        group: String,
        artifact: String,
        version: String,
        fileName: String,
        file: File
      ))
    else
      None
  }
}

case class DatabusModInput
(
  publisher: String,
  group: String,
  artifact: String,
  version: String,
  fileName: String,
  file: File
) {

  lazy val id: String = Array(publisher, group, artifact, version, fileName).mkString("/")

  def modMetadataFile(implicit basePath: String ): File = {
    File(basePath) / publisher / group / artifact / version / fileName / "mod.ttl"

  }

  def modErrorFile(implicit basePath: String ): File = {
    File(basePath) / publisher / group / artifact / version / fileName / ".FAILURE"
  }

  def modResourceFile(resourceName: String)(implicit basePath: String): File = {

    File(basePath) / publisher / group / artifact / version / fileName / resourceName
  }

  def isRunning: Boolean = {
    DatabusModInputQueue.getCurrent.contains(id)
  }

  override def toString: String =
    s"""$publisher
       |$group
       |$artifact
       |$version
       |$fileName
       |${file.pathAsString}
       |""".stripMargin

}
