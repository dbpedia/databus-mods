package org.dbpedia.databus.mods.core.model

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.vocabulary.RDF
import org.dbpedia.databus.mods.core.MetadataType
import org.dbpedia.databus.mods.core.model.vocabulary.{MOD, PROV}

import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.charset.StandardCharsets

// TODO try: generated ++ statSummary >= 1
class ModActivityMetadata(
  // rdf:type (prov:subClassOf mod:DatabusMod)
  val activityType: String,
  // mod:version
  val version: String,
  // prov:startedAtTime
  val startedAtTime: XSDDateTime,
  // prov:endedAtTime
  val endedAtTime: XSDDateTime,
  // prov:used
  val used: String,
  // prov:generated
  val generated: List[ModResult],
  // mod:statSummary
  val statSummary: Option[String],
) {

  def createRdfModel(base: URI): Model = {

    val model = ModelFactory.createDefaultModel()

    // TODO efficient?
    val triples =
      s"""@base <${base.toString}> .
         |
         |@prefix prov: <http://www.w3.org/ns/prov#> .
         |@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
         |@prefix mod: <http://dataid.dbpedia.org/ns/mod#> .
         |
         |<activity> <${RDF.`type`}> "$activityType" .
         |<activity> <${PROV.startedAtTime}> "$startedAtTime"^^xsd:dateTime .
         |<activity> <${PROV.endedAtTime}> "$endedAtTime"^^xsd:dateTime .
         |<activity> <${PROV.used}> <$used> .
         |<activity> <${MOD.version}> "$version" .
         |""".stripMargin

    model.read(new ByteArrayInputStream(triples.getBytes(StandardCharsets.UTF_8)), base.toString, "TURTLE")

    // add generated results
    // TODO better
    generated.foreach({
      modResult =>
        val modResultUri = base.relativize(modResult.id).toString
        model.add(
          ResourceFactory.createResource(base + "activity"),
          ResourceFactory.createProperty(PROV.generated),
          ResourceFactory.createResource(modResultUri)
        )
        model.add(
          ResourceFactory.createResource(modResultUri),
          ResourceFactory.createProperty(
            modResult.metadataType match {
              case MetadataType.Enrichment => MOD.enrichmentDerivedFrom
              case MetadataType.Statistics => MOD.statisticsDerivedFrom
              case MetadataType.Summary => MOD.summaryDerivedFrom
            }
          ),
          ResourceFactory.createResource(base + "activity"),
        )
        model.add(
          ResourceFactory.createResource(modResultUri),
          RDF.`type`,
          ResourceFactory.createResource(
            modResult.metadataType match {
              case MetadataType.Enrichment => MOD.Enrichment
              case MetadataType.Statistics => MOD.Statistics
              case MetadataType.Summary => MOD.Summary
            }
          ),
        )
    })

    // add statSummary
    statSummary.foreach({
      value =>
        model.add(
          ResourceFactory.createResource(base + "activity"),
          ResourceFactory.createProperty(MOD.statSummary),
          // TODO more flexible
          ResourceFactory.createStringLiteral(value)
        )
    })

    model
  }
}

object ModActivityMetadata {

  def builder(used: String = null): ModActivityMetadataBuilder = {
     ModActivityMetadataBuilder(used = used)
  }
}
