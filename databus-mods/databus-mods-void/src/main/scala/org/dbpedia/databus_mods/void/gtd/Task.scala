package org.dbpedia.databus_mods.void.gtd

import java.net.URI

case class Task
(
  account: String,
  group: String,
  artifact: String,
  version: String,
  distribution: String,
  source: URI
) {

  def dataIdFilePath: String =
   s"${account}/${group}/${artifact}/${version}/${distribution}"
}