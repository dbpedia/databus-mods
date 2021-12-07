package org.dbpedia.databus.mods.worker.springboot.service

import java.lang.Exception
import java.util.concurrent.Callable

import org.dbpedia.databus.dataid.SingleFile
import org.dbpedia.databus.mods.model.ModActivityMetadata

import scala.util.Random


class ActivityPlan(val dbusSF: SingleFile, activityProcessor: ModActivity) extends Callable[ModActivityMetadata] {

  override def call(): ModActivityMetadata = {
    val mam = new ModActivityMetadata(dbusSF)
    activityProcessor.process(mam)
    /*return*/ mam
  }
}
