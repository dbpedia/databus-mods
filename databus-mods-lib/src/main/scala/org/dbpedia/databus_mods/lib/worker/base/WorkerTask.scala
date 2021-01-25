package org.dbpedia.databus_mods.lib.worker.base

import java.net.URI

case class WorkerTask
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

  override def toString: String =
    s"""$account
       |$group
       |$artifact
       |$version
       |$distribution
       |$source
       |""".stripMargin
}