package org.dbpedia.databus_mods.server.core.persistence

import java.util

import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence._
import org.dbpedia.databus_mods.server.core.views.Views

import scala.annotation.meta.field
import scala.beans.BeanProperty

/**
 * Java representation of dataid core metadata for a file registered on the DBpedia databus
 * @param dataIdSingleFile
 * @param publisher
 * @param dataIdGroup
 * @param dataIdArtifact
 * @param dataIdVersion
 * @param downloadUrl
 * @param name
 * @param checksum
 * @param issued
 */
@Entity
@Table(
  name = "databus_file",
//  uniqueConstraints = Array(
//    // TODO
//    new UniqueConstraint(columnNames = Array("dataIdSingleFile", "checkSum")))
)
class DataIdPart
(
  // TODO rename to databusFileID or fileID
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  @(Column@field)(length = 4000)
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
  @(Column@field)(length = 4000)
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
  @(GeneratedValue@field)(strategy = GenerationType.IDENTITY)
  @BeanProperty
  @(JsonView@field)(value = Array(classOf[Views.Default]))
  var id: Long = _

//  @(JsonView@field)(value = Array(classOf[Views.DatabusFileView]))
//  @(OneToMany@field)(mappedBy = "databusFile", cascade = Array(CascadeType.ALL), fetch = FetchType.EAGER, orphanRemoval = true)
//  @BeanProperty
//  var tasks: java.util.List[Task] = new util.ArrayList[Task]()

  def this() {
    this(null, null, null, null, null, null, null, null, null)
  }

  def this(dataIdSingleFile: String, downloadUrl: String, checksum: String, issued: java.sql.Timestamp) {
    this(dataIdSingleFile, null, null, null, null, downloadUrl, null, checksum, issued)
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
       |""".stripMargin
    // + taskIds : ${tasks.map(_.getId).mkString(", ")}
  }

  def getDatabusPath: String = {
    List(publisher,dataIdGroup,dataIdArtifact,dataIdVersion,name).mkString("/")
  }

  def copyOf(df: DataIdPart): Unit = {
    setId(df.getId)
    setIssued(df.getIssued)
    setPublisher(df.getPublisher)
    setDataIdGroup(df.getDataIdGroup)
    setDataIdArtifact(df.getDataIdArtifact)
    setDataIdVersion(df.getDataIdVersion)
    setDownloadUrl(df.getDownloadUrl)
    setChecksum(df.getChecksum)
//    setTasks(df.getTasks)
  }
}
