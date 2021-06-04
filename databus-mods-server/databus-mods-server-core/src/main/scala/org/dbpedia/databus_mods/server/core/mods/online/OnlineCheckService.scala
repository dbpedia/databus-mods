package org.dbpedia.databus_mods.server.core.mods.online

import java.io.FileWriter
import java.net.URI
import java.sql.Timestamp
import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.{Calendar, Date, GregorianCalendar, Optional}

import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClientBuilder
import org.dbpedia.databus_mods.server.core.persistence.DatabusFile
import org.dbpedia.databus_mods.server.core.service.{DatabusFileService, FileService}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import scala.collection.JavaConversions._

@Service
class OnlineCheckService(databusFileService: DatabusFileService,
                         onlineCheckRepository: OnlineCheckRepository,
                         fileService: FileService,
                         @Value("${mod-server.schedule.online-check}") defaultExecutionDelay: String,
                         @Value("${mod-server.schedule.online-period}") onlinePeriod: String) {

  private val log = LoggerFactory.getLogger(classOf[OnlineCheckService])

  private val executionDelay = new AtomicInteger(defaultExecutionDelay.toInt)

  def setExecutionDelay(millis: Int): Unit = {
    if (millis > 15000)
      executionDelay.set(millis)
    else
      "Error: rate <= 15000"
  }

  def getNextExecutionTime(lastActualExecutionTime: Date): Date = {
    val nextExecutionTime = new GregorianCalendar
    nextExecutionTime.setTime(if (lastActualExecutionTime != null) lastActualExecutionTime else new Date())
    nextExecutionTime.add(Calendar.MILLISECOND, executionDelay.get()) //you can get the value from wherever you want
    nextExecutionTime.getTime
  }

  private val updateLock = new AtomicBoolean(false)

  def update(): Unit = {
    if (updateLock.get()) {
      log.info("skip still running")
    } else {
      log.info("update")
      databusFileService.getAll
        //        .filter(_.getPublisher == "ontologies")
        .foreach({
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
            val onlineCheck = new OnlineCheck(databusFile, code)

            val possibleLastOnlineCheck =
              onlineCheckRepository.findFirstByDatabusFileDataIdSingleFileOrderByTimestampDesc(databusFile.getDataIdSingleFile)

            var append = false
            if (possibleLastOnlineCheck.isPresent) {
              if (possibleLastOnlineCheck.get.status != onlineCheck.status) {
                onlineCheckRepository.save(onlineCheck)
                append = true
              }
            } else {
              onlineCheckRepository.save(onlineCheck)
              append = true
            }
            updateMetadata(databusFile, onlineCheck, append, possibleLastOnlineCheck)

            client.close()
        })
      log.info("updated")
    }
  }

  private def updateMetadata(databusFile: DatabusFile, latestCheck: OnlineCheck, append: Boolean, priorCheck: Optional[OnlineCheck]): Unit = {

    val csv = fileService.getOrCreate("online", databusFile.getDatabusPath, "online.csv")
    if (append) {
      val csvWriter = new FileWriter(csv, true)
      val entry = latestCheck.getTimestamp + ";" + latestCheck.getStatus + "\n"
      csvWriter.write(entry)
      csvWriter.flush()
      csvWriter.close()
    }

    val minTimestamp = new Timestamp(latestCheck.timestamp.getTime - onlinePeriod.toLong)
    val changes = onlineCheckRepository.findByTimestampGreaterThanOrderByTimestamp(minTimestamp)

    if (changes.isEmpty) {
      changes.add(latestCheck)
    }

    var current = minTimestamp.getTime
    var up = 0L
    changes.foreach({
      onlineChange =>
        if (onlineChange.status == 200) {
          up += onlineChange.getTimestamp.getTime - current
        }
        current = onlineChange.getTimestamp.getTime
    })
    if (latestCheck.getStatus == 200)
      up += latestCheck.timestamp.getTime - current
    val uptime: Double = up / onlinePeriod.toDouble

    //    if(changes.size() == 1 && priorCheck.isPresent && (priorCheck.get().status == latestCheck.status)) {
    //
    //    } else {
    //    TODO lower writes
    val svg = fileService.getOrCreate("online", databusFile.getDatabusPath, "online.svg")
    val svgWriter = new FileWriter(svg)
    svgWriter.write(createSVG(uptime))
    svgWriter.flush()
    svgWriter.close()
    //    }
  }

  private def createSVG(stat: Double): String = {
    val statString = (stat * 100).toString
    val roundStat = statString.split("\\.").head
    val color = {
      if (stat > 0.99) {
        "#97ca00" //green
      } else if (stat > 0.95) {
        "#007ec6" //blue
      } else if (stat > 0.50) {
        "#dfb317" //yellow
      } else {
        "#e05d44" //red
      }
    }
    val Array(x, width1, width2, textLength) = {
          if(stat == 1.0) {
            Array(635,43,86,330) //100%
          } else if(stat > 0.1) {
            Array(595,35,78,250) //10%
          } else {
            Array(565,29,72,190) //1%
          }
        }

    s"""<?xml version="1.0"?>
       |<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="86" height="20" role="img" aria-label="online: 100%">
       |  <title>online: 100%</title>
       |  <linearGradient id="s" x2="0" y2="100%">
       |    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
       |    <stop offset="1" stop-opacity=".1"/>
       |  </linearGradient>
       |  <clipPath id="r">
       |    <rect width="$width2" height="20" rx="3" fill="#fff"/>
       |  </clipPath>
       |  <g clip-path="url(#r)">
       |    <rect width="43" height="20" fill="#555"/>
       |    <rect x="43" width="$width1" height="20" fill="$color"/>
       |    <rect width="$width2" height="20" fill="url(#s)"/>
       |  </g>
       |  <g fill="#fff" text-anchor="middle" font-family="Verdana,Geneva,DejaVu Sans,sans-serif" text-rendering="geometricPrecision" font-size="110">
       |    <text aria-hidden="true" x="225" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="330">online</text>
       |    <text x="225" y="140" transform="scale(.1)" fill="#fff" textLength="330">online</text>
       |    <text aria-hidden="true" x="$x" y="150" fill="#010101" fill-opacity=".3" transform="scale(.1)" textLength="$textLength">$roundStat%</text>
       |    <text x="$x" y="140" transform="scale(.1)" fill="#fff" textLength="$textLength">$roundStat%</text>
       |  </g>
       |</svg>""".stripMargin
  }

  def getAll: util.Iterator[OnlineCheck] = {
    onlineCheckRepository.findAll().iterator()
  }
}


