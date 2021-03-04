package org.dbpedia.databus_mods.server.core.persistence

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import org.dbpedia.databus_mods.server.core.views.Views
import org.hibernate.annotations.{Generated, GenerationTime}

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(
  name = "task",
  uniqueConstraints = Array(
    new UniqueConstraint(columnNames = Array("databusFile_id", "mod_id"))))
class Task
(
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @(JoinColumn@field)(name = "databusFile_id")
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicTaskView]))
  var databusFile: DatabusFile,
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicTaskView],classOf[Views.DatabusFileView]))
  var mod: Mod
) {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var id: Long = _

  @PreRemove
  def removeTaskFromDatabusFile(): Unit = {
    databusFile.getTasks.remove(this)
  }

//  @BeanProperty
//  var uri: String = _

  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var state: Int = _

  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @(JoinColumn@field)(name = "worker_id")
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.PublicTaskView]))
  var worker: Worker = _

  //  @Transient
//  @Basic
//  private var statusValue: Int = _
//
//  @PostLoad
//  def fillTransient(): Unit = {
//    this.state = Status(statusValue)
//  }

//  @PrePersist
//  def fillPersist(): Unit = {
//    println(state)
//    this.statusValue = state.id
//  }

  def copyOf(t: Task): Unit = {
    setId(t.getId)
    setState(t.getState)
    setDatabusFile(t.getDatabusFile)
    setMod(t.getMod)
  }

  def this() {
    this(null, null)
  }

    override def toString: String = {
      s"""TASK#$id(
         |  databusFile.id : ${databusFile.getId},
         |  databusFile.dataIdSingleFile : ${databusFile.getDataIdSingleFile},
         |  mod.id : ${mod.getId},
         |  mod.name : ${mod.getName})""".stripMargin
    }
}
