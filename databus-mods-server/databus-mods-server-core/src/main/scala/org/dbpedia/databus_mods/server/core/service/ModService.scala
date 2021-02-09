package org.dbpedia.databus_mods.server.core.service

import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository, Worker, WorkerRepository}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._


@Service
class ModService {

  private val log = LoggerFactory.getLogger(classOf[ModService])

  @Autowired private var modRepository: ModRepository = _

  @Autowired private var workerRepository: WorkerRepository = _

  def addMod(name: String, query: String, workerApis: java.util.ArrayList[String]) = {

    var mod = modRepository.findByName(name)
    if(null == mod) {
      mod = new Mod(name,query)
      modRepository.save(mod)
      workerApis.foreach({
        workerApi =>
          if( null == workerRepository.findByAddr(workerApi))
            workerRepository.save(new Worker(mod,workerApi))
          else
            log.warn("addMod: duplicate worker entry; worker was not added")
      })
      modRepository.save(mod)
    } else {
      // TODO
    }
  }

  def deleteMod(name: String) = {
    val mod = modRepository.findByName(name)
    if(null != mod )
      modRepository.delete(mod)
  }

  def deleteAll() = {
    modRepository.deleteAll()
  }
}
