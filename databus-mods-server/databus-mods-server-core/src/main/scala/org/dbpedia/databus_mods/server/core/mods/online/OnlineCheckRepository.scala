//package org.dbpedia.databus_mods.server.core.mods.online
//
//import java.sql.Timestamp
//import java.util.Optional
//
//import org.springframework.data.repository.CrudRepository
//import org.springframework.stereotype.Repository
//
//@Repository
//trait OnlineCheckRepository extends CrudRepository[OnlineCheck, Long] {
//
//
//  def findFirstByDatabusFileDataIdSingleFileOrderByTimestampDesc(databusFileDataIdSingleFile: String): Optional[OnlineCheck]
//
//
//  def findByTimestampGreaterThanOrderByTimestamp(timestamp: Timestamp): java.util.List[OnlineCheck]
//}
