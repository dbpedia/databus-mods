package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(name = "mod")
class Mod
(
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicModView], classOf[Views.PublicWorkerView]))
  var name: String,
  @BeanProperty
  @(Column@field)(length = 10000)
  @(JsonView@field)(value = Array(classOf[Views.PublicModView]))
  var query: String
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @(JsonView@field)(value = Array(classOf[Views.PublicModView], classOf[Views.PublicWorkerView]))
  @BeanProperty
  var id: Long = _

  @(OneToMany@field)(mappedBy = "mod", cascade = Array(CascadeType.ALL), fetch = FetchType.LAZY, orphanRemoval = true)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicModView]))
  var worker: java.util.List[Worker] = new util.ArrayList[Worker]()

  def this() {
    this(null, null)
  }

  def info: String = {

    import scala.collection.JavaConversions._
    s"""Mod$id
       |+ services : ${worker.mkString(", ")}
       |+ query : $query
       |""".stripMargin
  }
}
