package org.dbpedia.databus.mods.core.model

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.dbpedia.databus.mods.core.model.vocabulary.MOD

import java.time.Instant
import java.util.Calendar

// TODO scala way for Builder?
case class ModActivityMetadataBuilder(
  // rdf:type (prov:subClassOf mod:DatabusMod)
  private var activityType: String = MOD.DatabusMod,
  // mod:version
  private var version: String = "1.0.0",
  // prov:startedAtTime
  private var startedAtTime: XSDDateTime = null,
  // prov:endedAtTime xsd:dateTime
  private var endedAtTime: XSDDateTime = null,
  // prov:used
  private var used: String,
  // prov:generated
  private var generated: List[ModResult] = List(),
  // mod:statSummary
  private var statSummary: Option[String] = None,
) {

  def withType(iri: String): ModActivityMetadataBuilder = {
    this.activityType = iri
    this
  }

  def withVersion(version: String): ModActivityMetadataBuilder = {
    this.version = version
    this
  }

  def withStartedAtTime(datetime: XSDDateTime): ModActivityMetadataBuilder = {
    this.startedAtTime = datetime
    this
  }

  def withEndedAtTime(datetime: XSDDateTime): ModActivityMetadataBuilder = {
    this.endedAtTime = datetime
    this
  }

  def addGenerated(generated: ModResult): ModActivityMetadataBuilder = {
    this.generated = this.generated ++ List(generated)
    this
  }

  def withStatSummary(value: String /*, TODO property: String = MOD.statSummary */): ModActivityMetadataBuilder = {
    this.statSummary = Some(value)
    this
  }

  def build(): ModActivityMetadata = {

    val finalEndedAtTime = {
      if(null == this.endedAtTime) {
        new XSDDateTime(Calendar.getInstance())
      } else {
        this.endedAtTime
      }
    }

    new ModActivityMetadata(
      activityType = this.activityType,
      version = this.version,
      startedAtTime = this.startedAtTime,
      endedAtTime = finalEndedAtTime,
      used = this.used,
      generated = this.generated,
      statSummary = this.statSummary
    )
  }
}
