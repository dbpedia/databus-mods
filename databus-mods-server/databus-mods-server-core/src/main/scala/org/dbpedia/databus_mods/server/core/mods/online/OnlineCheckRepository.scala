package org.dbpedia.databus_mods.server.core.mods.online

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
trait OnlineCheckRepository extends CrudRepository[OnlineCheck, Long] {


}
