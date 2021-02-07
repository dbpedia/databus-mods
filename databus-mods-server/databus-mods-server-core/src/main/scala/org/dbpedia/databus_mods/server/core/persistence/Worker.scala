package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence.{CascadeType, Entity, FetchType, GeneratedValue, GenerationType, Id, ManyToOne, OneToMany, Table}
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(name = "worker")
class Worker
(
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicWorkerView]))
  var mod: Mod,
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicModView], classOf[Views.PublicWorkerView]))
  var addr: String
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicModView], classOf[Views.PublicWorkerView]))
  var id: Long = _

  def this() {
    this(null, null)
  }
}
