package org.dbpedia.databus_mods.server.core.mods.online

import java.sql.Timestamp

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence.{Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, Temporal, TemporalType}
import org.dbpedia.databus_mods.server.core.persistence.DatabusFile
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity(name = "online_check")
class OnlineCheck
(
  @(JsonView@field)(value = Array(classOf[Views.PublicOnlineCheck]))
  @(ManyToOne@field)(fetch = FetchType.EAGER)
  @(JoinColumn@field)(name = "databusFile_id")
  @BeanProperty
  var databusFile: DatabusFile,
  @(JsonView@field)(value = Array(classOf[Views.PublicOnlineCheck]))
  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP)
  var timestamp: Timestamp,
  @(JsonView@field)(value = Array(classOf[Views.PublicOnlineCheck]))
  @BeanProperty
  var status: Int
) {
  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @BeanProperty
  var id: Long = _

  def this() {
    this(null, null, 200)
  }

  def this(databusFile: DatabusFile, status: Int) {
    this(databusFile,new Timestamp(System.currentTimeMillis()),status)
  }

  override def toString: String = {
    s"""$timestamp; $status ;${databusFile.getDataIdSingleFile}"""
  }
}

