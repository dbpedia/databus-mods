package org.dbpedia.databus_mods.server.core.persistence

import javax.persistence._
import org.hibernate.annotations.{Generated, GenerationTime}

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(
  name = "task"
//  uniqueConstraints = Array(
//    new UniqueConstraint(columnNames = Array("databusFileId", "modId"))
//  ))
)
class Task
(
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @BeanProperty
  var databusFile: DatabusFile,
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @BeanProperty
  var mod: Mod
) {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var uri: String = _

  @BeanProperty
  var state: Int = _

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

  def this() {
    this(null, null)
  }

  //  override def toString: String = {
  //    s"""TASK#$id
  //       |+ databusFile.id : ${databusFile.getId}
  //       |+ databusFile.dataIdSingleFile : ${databusFile.getDataIdSingleFile}
  //       |+ mod.id : ${mod.getId}
  //       |+ mod.name : ${mod.getName}
  //       |""".stripMargin
  //  }
}
