package org.dbpedia.databus_mods.server.core.service

import java.util.Optional
import java.util

import org.dbpedia.databus_mods.server.core.persistence.{DatabusFile, DatabusFileRepository}
import org.springframework.stereotype.Service

@Service
class DatabusFileService(
                        databusFileRepository: DatabusFileRepository) {

  def add(df: DatabusFile): Unit = {
    val databusFile = databusFileRepository.findByDataIdSingleFileAndChecksum(df.dataIdSingleFile, df.checksum)
    if(databusFile.isPresent) {
      df.copyOf(databusFile.get)
    } else {
      databusFileRepository.save(df)
    }
  }

  def get(databusId: String, checksum: String): Optional[DatabusFile] = {
    databusFileRepository.findByDataIdSingleFileAndChecksum(databusId,checksum)
  }

  def getAll: util.Iterator[DatabusFile] = {
    databusFileRepository.findAll().iterator()
  }
}
