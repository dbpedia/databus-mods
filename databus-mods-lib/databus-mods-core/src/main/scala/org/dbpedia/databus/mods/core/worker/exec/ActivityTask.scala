package org.dbpedia.databus.mods.core.worker.exec

import org.apache.jena.datatypes.xsd.XSDDateTime
import org.dbpedia.databus.mods.core.model.vocabulary.MOD
import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityRequest}

import java.util.Calendar
import java.util.concurrent.Callable

class ActivityTask (
  activityRequest: ModActivityRequest,
  modActivity: ModActivity
  ) extends Callable[ModActivityMetadata] {

    override def call(): ModActivityMetadata = {

      val builder =
        ModActivityMetadata.builder(activityRequest.dataId)
          .withType(MOD.DatabusMod)
          .withVersion("1.0")
          .withStartedAtTime(new XSDDateTime(Calendar.getInstance()))

      modActivity.perform(activityRequest, builder)
  }
}
