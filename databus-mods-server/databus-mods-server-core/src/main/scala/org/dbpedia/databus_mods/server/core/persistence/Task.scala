package org.dbpedia.databus_mods.server.core.persistence

import javax.persistence._

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(name = "task")
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
  @Transient
  var status: Status.Value = Status.Open

  @Basic
  private var statusValue: Int = _

  @PostLoad
  def fillTransient(): Unit = {
    this.status = Status(statusValue)
  }

  @PrePersist
  def fillPersist(): Unit = {
    this.statusValue = status.id
  }

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
