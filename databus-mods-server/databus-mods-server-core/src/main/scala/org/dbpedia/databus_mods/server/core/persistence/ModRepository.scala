package org.dbpedia.databus_mods.server.core.persistence

import javax.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@Transactional
trait ModRepository extends CrudRepository[Mod,Long]
