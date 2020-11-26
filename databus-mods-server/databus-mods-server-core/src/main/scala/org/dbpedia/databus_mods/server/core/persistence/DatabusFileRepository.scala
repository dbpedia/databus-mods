package org.dbpedia.databus_mods.server.core.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
trait DatabusFileRepository extends CrudRepository[DatabusFile,Long] {

  def findByDataIdSingleFileAndChecksum(dataIdSingleFile: String, checksum: String): DatabusFile


}
