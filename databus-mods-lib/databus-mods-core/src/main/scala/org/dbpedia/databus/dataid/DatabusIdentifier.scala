package org.dbpedia.databus.dataid

import scala.util.matching.Regex

class DatabusIdentifier(
                         val publisher: String,
                         val group: String,
                         val artifact: String,
                         val version: String,
                         val file: String,
                         val base: String = "https://databus.dbpedia.org") {

  lazy val idType: DatabusIdentifierType.Value = {
    List(publisher, group, artifact, version, file).count(_ != null) match {
      case 1 => DatabusIdentifierType.PUBLISHER_ID
      case 2 => DatabusIdentifierType.GROUP_ID
      case 3 => DatabusIdentifierType.ARTIFACT_ID
      case 4 => DatabusIdentifierType.VERSION_ID
      case 5 => DatabusIdentifierType.FILE_ID
    }
  }

  lazy val id: String = base + "/" + List(publisher, group, artifact, version, file).filter(_ != null).mkString("/")

  def getPublisherID: String = base + "/" + publisher

  def getGroupID: String = if (null != group) getPublisherID + "/" + group else null

  def getArtifactID: String = if (null != artifact) getGroupID + "/" + artifact else null

  def getVersionID: String = if (null != version) getVersionID + "/" + version else null

  def getFileID: String = if (null != file) getVersionID + "/" + file else null

  override def toString: String = s"DatabusIdentifier($idType($base/" +
    List(publisher, group, artifact, version, file).filter(_ != null).mkString("/") + "))"
}

object DatabusIdentifier {

  val PATTERN: Regex = "^https://databus\\.dbpedia\\.org/([^/]+)/?([^/]+)?/?([^/]+)?/?([^/]+)?/?([^/]+)?$".r

  def apply(string: String, base: String = "https://databus.dbpedia.org"): Option[DatabusIdentifier] = {

    string match {
      case PATTERN(p, g, a, v, f) => Some(new DatabusIdentifier(p, g, a, v, f))
      case _ => None
    }
  }

  def main(args: Array[String]): Unit = {

    val validDatabusIDStrings = Array(
      "https://databus.dbpedia.org",
      "https://databus.dbpedia.org/dbpedia",
      "https://databus.dbpedia.org/dbpedia/databus",
      "https://databus.dbpedia.org/dbpedia/databus/databus-data",
      "https://databus.dbpedia.org/dbpedia/databus/databus-data/2021.01.31",
      "https://databus.dbpedia.org/dbpedia/databus/databus-data/2021.01.31/databus-data.nt.bz2",
      "https://databus.dbpedia.org/dbpedia/databus/databus-data/2021.01.31/databus-data.nt.bz2/foo"
    )

    validDatabusIDStrings.foreach({
      str =>
        DatabusIdentifier(str) match {
          case Some(x) => println(x)
          case None => println("None")
        }
        println()
    })
  }
}
