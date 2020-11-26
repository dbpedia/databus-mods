package org.dbpedia.databus_mods.server.core.persistence

import java.util

import javax.persistence._

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(name = "mod")
class Mod
(
  @BeanProperty
  var name: String,
  @BeanProperty
  @(Column@field)(length = 10000)
  var query: String
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @BeanProperty
  var id: Long = _

  @BeanProperty
  @ElementCollection(fetch = FetchType.EAGER)
  var services: java.util.List[String] = new util.ArrayList[String]()

  def this() {
    this(null, null)
  }

  def info: String = {

    import scala.collection.JavaConversions._
    s"""Mod$id
       |+ services : ${services.mkString(", ")}
       |+ query : $query
       |""".stripMargin
  }
}
