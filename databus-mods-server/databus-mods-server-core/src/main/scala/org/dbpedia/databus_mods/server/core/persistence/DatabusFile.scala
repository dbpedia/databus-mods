package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import javax.persistence._
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

@Entity
@Table(
  name = "databus_file",
  uniqueConstraints = Array(
    // TODO
    new UniqueConstraint(columnNames = Array("dataIdSingleFile", "checkSum"))))
class DatabusFile
(
  // TODO rename to databusFileID or fileID
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var dataIdSingleFile: String,
  @BeanProperty
  var publisher: String,
  @BeanProperty
  var dataIdGroup: String,
  @BeanProperty
  var dataIdArtifact: String,
  @BeanProperty
  var dataIdVersion: String,
  @BeanProperty
  var downloadUrl: String,
  @BeanProperty
  var name: String,
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var checksum: String,
  @BeanProperty
  @Temporal(TemporalType.TIMESTAMP)
  var issued: java.sql.Timestamp
) {

  @(Id@field)
  @(GeneratedValue@field)(strategy = GenerationType.TABLE)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var id: Long = _

  @(JsonView@field)(value = Array(classOf[Views.DatabusFileView]))
//  @(OneToMany@field)(mappedBy = "databusFile", cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
  @(OneToMany@field)(mappedBy = "databusFile", cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
  @BeanProperty
  var tasks: java.util.List[Task] = new util.ArrayList[Task]()

  def this() {
    this(null, null, null, null, null, null, null, null, null)
  }

  def this(dataIdSingleFile: String, downloadUrl: String, checksum: String, issued: java.sql.Timestamp) {
    this(dataIdSingleFile, null, null, null, null, null, downloadUrl, checksum, issued)
    val Array(p, g, a, v, f) = dataIdSingleFile.split('/').drop(3)
    this.publisher = p
    this.dataIdGroup = g
    this.dataIdArtifact = a
    this.dataIdVersion = v
    this.name = f
    this.issued = issued
  }

  override def toString: String = {
    import scala.collection.JavaConversions._
    s"""DATABUSFILE#$id
       |+ dataIdSingleFile : $dataIdSingleFile
       |+ issued : $issued
       |+ publisher : $publisher
       |+ dataIdGroup : $dataIdGroup
       |+ dataIdArtifact : $dataIdArtifact
       |+ dataIdVersion : $dataIdVersion
       |+ downloadUrl : $downloadUrl
       |+ checksum : $checksum
       |+ taskIds : ${tasks.map(_.getId).mkString(", ")}
       |""".stripMargin
  }

  def copyOf(df: DatabusFile): Unit = {
    setId(df.getId)
    setIssued(df.getIssued)
    setPublisher(df.getPublisher)
    setDataIdGroup(df.getDataIdGroup)
    setDataIdArtifact(df.getDataIdArtifact)
    setDataIdVersion(df.getDataIdVersion)
    setDownloadUrl(df.getDownloadUrl)
    setChecksum(df.getChecksum)
    setTasks(df.getTasks)
  }
}
