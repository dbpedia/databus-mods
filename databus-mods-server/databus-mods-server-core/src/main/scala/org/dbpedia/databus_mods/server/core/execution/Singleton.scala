package org.dbpedia.databus_mods.server.core.execution

import org.dbpedia.databus_mods.server.core.service.{MetadataService, TaskService}

object Singleton {

  var taskService: TaskService = _

  var metadataService: MetadataService = _

}
