package org.dbpedia.databus_mods.server.core.execution

import org.apache.jena.rdf.model.Model

case class ModMetadata(statusCode: Int, metadata: Option[Model])
