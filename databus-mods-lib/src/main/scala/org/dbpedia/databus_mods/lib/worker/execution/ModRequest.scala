package org.dbpedia.databus_mods.lib.worker.execution

import java.net.URI

case class ModRequest(databusID: String, sourceURI: String) {

  lazy val databusPath: String = new URI(databusID).getPath
}