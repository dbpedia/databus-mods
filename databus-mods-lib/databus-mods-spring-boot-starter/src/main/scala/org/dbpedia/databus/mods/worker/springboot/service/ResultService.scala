package org.dbpedia.databus.mods.worker.springboot.service

import org.dbpedia.databus.dataid.Part

import java.io.{InputStream, OutputStream}

trait ResultService {

  def openResultInputStream(didPart: Part, resultName: String): Option[InputStream]

  def openResultOutputStream(didPart: Part, resultName: String): Option[OutputStream]
}
