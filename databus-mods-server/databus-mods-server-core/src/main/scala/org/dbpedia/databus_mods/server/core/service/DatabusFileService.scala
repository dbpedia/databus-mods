package org.dbpedia.databus_mods.server.core.service

import org.dbpedia.databus_mods.server.core.persistence.DatabusFileRepository
import org.springframework.stereotype.Service

@Service
class DatabusFileService(
                        databusFileRepository: DatabusFileRepository) {

  def getDatabusFiles() = {
    databusFileRepository.findAll()
  }
}
