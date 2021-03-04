package org.dbpedia.databus_mods.server.core.config.mods

import scala.beans.BeanProperty
import java.util.{ArrayList => JArrayList}

case class ModConfig() {
  @BeanProperty
  var name: String = _

  @BeanProperty
  var query: String = _

  @BeanProperty
  val selects: JArrayList[String] = new JArrayList[String]()

  @BeanProperty
  var accepts: JArrayList[String] = new JArrayList[String]()

  @BeanProperty
  var workers: JArrayList[String] = new JArrayList[String]()
}

