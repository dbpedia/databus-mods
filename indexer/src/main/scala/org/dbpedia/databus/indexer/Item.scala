package org.dbpedia.databus.indexer

import java.net.URL


class Item(
            val shaSum: String,
            val downloadURL: URL,
            val dataset: URL,
            val version: URL,
            val distribution: URL

          ) extends scala.Serializable {

  def factory() = {}
}
