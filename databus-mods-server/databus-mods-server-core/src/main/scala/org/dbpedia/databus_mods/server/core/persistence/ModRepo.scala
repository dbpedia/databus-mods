package org.dbpedia.databus_mods.server.core.persistence

import jakarta.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
@Transactional
trait ModRepository extends CrudRepository[Mod,Long] {

  def findByName(name: String): Optional[Mod]
}
