package org.dbpedia.databus.mods.model

abstract class ModActivity {

  def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata
}