package org.dbpedia.databus_mods.server.core.mods.online

import java.net.{URI, URL}
import java.util.Date
import java.util

import org.apache.http.HttpResponse
import org.apache.http.client.{HttpClient, ResponseHandler}
import org.dbpedia.databus_mods.server.core.service.DatabusFileService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.apache.http.client.fluent.Request
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClientBuilder

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

@Service
class OnlineCheckService(databusFileService: DatabusFileService,
                        onlineCheckRepository: OnlineCheckRepository,
                        @Value("${mod-server.schedule.online-check}") defaultExecutionDelay: String) {

  private val log = LoggerFactory.getLogger(classOf[OnlineCheckService])

  private val executionDelay = new AtomicInteger(defaultExecutionDelay.toInt)

  def setExecutionDelay(millis: Int) = {
    if(millis > 15000)
      executionDelay.set(millis)
    else
      "Error: rate <= 15000"
  }

  def getNextExecutionTime(lastActualExecutionTime: Date): Date = {
    val nextExecutionTime = new GregorianCalendar
    nextExecutionTime.setTime(if(lastActualExecutionTime!= null) lastActualExecutionTime else new Date())
    nextExecutionTime.add(Calendar.MILLISECOND, executionDelay.get()) //you can get the value from wherever you want
    nextExecutionTime.getTime
  }

  def update(): Unit = {
    log.info("begin update")
    databusFileService.getAll.foreach({
      databusFile =>
        val url = new URI(databusFile.getDownloadUrl)
        val client = HttpClientBuilder.create().build()
        var code = -1
        try {
          val response = client.execute(new HttpHead(url))
          code = response.getStatusLine.getStatusCode
          response.close()
        } catch {
          case e: Exception =>
            log.warn(s"s error check $url")
        }
        val onlineCheck = new OnlineCheck(databusFile,code)
        onlineCheckRepository.save(onlineCheck)
        client.close()
    })
    log.info("end update")
  }

  def getAll: util.Iterator[OnlineCheck] = {
    onlineCheckRepository.findAll().iterator()
  }
}


