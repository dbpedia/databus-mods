package org.dbpedia.databus_mods.server.core.persistence

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
trait TaskRepository extends CrudRepository[Task,Long] {

  def findTop10ByModNameOrderByDatabusFileIssuedDesc(modName: String): Task

  def findByModNameOrderByDatabusFileIssuedDesc(modName: String): java.util.List[Task]

  def findByModNameAndStateOrderByDatabusFileIssuedDesc(modName: String, state: Int): java.util.List[Task]
}
