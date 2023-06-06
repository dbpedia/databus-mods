package org.dbpedia.databus.mods.core.model

abstract class ModActivity {

  def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata
}