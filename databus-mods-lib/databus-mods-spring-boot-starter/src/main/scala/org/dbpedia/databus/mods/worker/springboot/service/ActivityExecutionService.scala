package org.dbpedia.databus.mods.worker.springboot.service

import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.core.worker.exec.ActivityExecution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.util.concurrent._

//@Service
class ActivityExecutionService(modActivity: ModActivity) extends ActivityExecution(modActivity) {}