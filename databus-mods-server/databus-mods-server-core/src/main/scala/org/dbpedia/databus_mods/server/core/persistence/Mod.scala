package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(
  name = "mod",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("name"))))
class Mod
(
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var name: String,
  @BeanProperty
  @(Column@field)(length = 10000)
  @(JsonView@field)(value = Array(classOf[Views.PublicModView]))
  var query: String
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  @BeanProperty
  var id: Long = _

  @(OneToMany@field)(mappedBy = "mod", fetch = FetchType.EAGER)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicModView]))
  var workers: java.util.List[Worker] = new util.ArrayList[Worker]()

  def this() {
    this(null, null)
  }
  override def toString: String = {
    import scala.collection.JavaConversions._
    s"""MOD#$id
       |+ name: $name
       |+ services : ${workers.mkString(", ")}
       |+ query : $query
       |""".stripMargin
  }

  def copyOf(m: Mod): Unit = {
    setId(m.getId)
    setName(m.getName)
    setQuery(m.getQuery)
    setWorkers(m.getWorkers)
  }
}
