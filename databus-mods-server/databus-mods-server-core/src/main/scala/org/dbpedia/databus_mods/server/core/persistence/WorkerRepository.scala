package org.dbpedia.databus_mods.server.core.persistence

import java.util.Optional

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait WorkerRepository extends CrudRepository[Worker,Long] {

  def findByUrl(url: String): Optional[Worker]
}
