package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence.{CascadeType, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, OneToMany, Table, UniqueConstraint}
import org.apache.commons.lang.SerializationUtils
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(
  name = "worker",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("url"))))
class Worker
(
  @(ManyToOne@field)
  @JoinColumn(name = "mod_id")
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicWorkerView]))
  var mod: Mod,
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var url: String
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var id: Long = _

  def this() {
    this(null, null)
  }

  override def toString: String =
    s"""WORKER#$id(mod.id = ${mod.id}, url = $url)""".stripMargin

  def copyOf(w: Worker) : Unit = {
    setId(w.getId)
    setUrl(w.getUrl)
    setMod(w.getMod)
  }
}
