package org.dbpedia.databus.mods.worker.springboot.service

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.vocabulary.XSD
import org.dbpedia.databus.mods.model.vocabulary.MOD
import org.dbpedia.databus.mods.model.{ModActivity, ModActivityMetadata, ModActivityRequest}

import java.util.Calendar
import java.util.concurrent.Callable

class ActivityRunner(
  activityRequest: ModActivityRequest,
  modActivity: ModActivity
) extends Callable[ModActivityMetadata] {

  override def call(): ModActivityMetadata = {

    val builder =
      ModActivityMetadata.builder(activityRequest.id)
        .withType(MOD.DatabusMod)
        .withVersion("1.0")
        .withStartedAtTime(new XSDDateTime(Calendar.getInstance()))

    modActivity.perform(activityRequest, builder)
  }
}
