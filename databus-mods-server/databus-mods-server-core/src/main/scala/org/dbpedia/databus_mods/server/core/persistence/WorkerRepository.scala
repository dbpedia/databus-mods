package org.dbpedia.databus_mods.server.core.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait WorkerRepository extends CrudRepository[Worker,Long] {


}
