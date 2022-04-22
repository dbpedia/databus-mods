package org.dbpedia.databus.mods.worker.springboot.service

import org.dbpedia.databus.mods.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.graalvm.compiler.lir.CompositeValue.Component

@Component
class DefaultModActivity extends ModActivity {

  override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {
    builder.withStatSummary("1.0").build()
  }
}
