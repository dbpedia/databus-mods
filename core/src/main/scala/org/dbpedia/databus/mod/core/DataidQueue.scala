package org.dbpedia.databus.mod.core

import java.util.concurrent.LinkedBlockingQueue

import org.slf4j.LoggerFactory

object DataidQueue {

  private val log = LoggerFactory.getLogger(getClass)

  private val q = new LinkedBlockingQueue[String]();

  def put(dataid: String): Unit = {
    log.info(s"enqueued $dataid")
    q.put(dataid)
  }

  def take(): String = {
    val dataid = q.take()
    log.info(s"dequeued $dataid")
    dataid
  }
}
