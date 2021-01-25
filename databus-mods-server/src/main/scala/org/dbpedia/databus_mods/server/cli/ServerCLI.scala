package org.dbpedia.databus_mods.server.cli

import java.io.{DataOutputStream, File, InputStreamReader}
import java.net.{HttpURLConnection, URI, URL, URLEncoder}
import java.{util => ju}
import java.util.concurrent.LinkedBlockingDeque

import org.dbpedia.databus_mods.server.database.DatabusFile
import org.slf4j.LoggerFactory

import sys.process._
import scala.io.Source

/**
 * SPO failed 'ontologies/purl.bdrc.io/ontology--core/2020.08.15-020351/ontology--core_type=diff_axioms=new.nt'
 *
 * */

class ServerCLI
object ServerCLI extends App {

  /* args */
  val queryFilePath = args(0)
  val modUrl = args(1)
  val hostCachePath = args(2)
  val modCachePath = args(3)

  /* core */
  val log = LoggerFactory.getLogger(classOf[ServerCLI])
  val downloadQueue = new LinkedBlockingDeque[DatabusFile]()
  val processQueue = new LinkedBlockingDeque[DatabusFile]()

  /* logic */

  val querySource = Source.fromFile(queryFilePath)
  val queryString = querySource.getLines().mkString("\n")
  querySource.close()

  log.info("start query databus")
//  DatabusQueryUtil.getUpdates(queryString).foreach(downloadQueue.add)
  log.info(s"finished query databus ${downloadQueue.size}")

  new Thread(new DownloadThread(downloadQueue,new File(hostCachePath),processQueue)).start()
  new Thread(new ProcessThread(processQueue,new URL(modUrl), new File(modCachePath).toURI)).run()

  class DownloadThread(downloadQueue: LinkedBlockingDeque[DatabusFile],
                       sinkDir: File,
                       processQueue: LinkedBlockingDeque[DatabusFile])
    extends Runnable {

    private var dId = 0
    private val log = LoggerFactory.getLogger(classOf[DownloadThread])


    override def run(): Unit = {
      log.info(downloadQueue.size().toString)
      while (true) {
        val databusFile = downloadQueue.take()
        log.info(s"start download #$dId ${databusFile.id}")
        val targetFile = new File(sinkDir,databusFile.id)
        urlToFile(new URL(databusFile.downloadUrl), targetFile)
        log.info(s"finished download #$dId ${databusFile.id}")
        processQueue.add(databusFile)
        dId += 1
      }
    }

    def urlToFile(url: URL, file: File): Boolean = {
      if(file.exists()) return true
      try {
        file.getParentFile.mkdirs()
        if( 0 != (url #> file).!) false
        else true
      } catch {
        case e: Exception => false
      }
    }
  }

  class ProcessThread(processQueue: LinkedBlockingDeque[DatabusFile],
                      modUrl: URL,
                      baseUri: URI)
    extends Runnable {

    private var pId = 0
    private val log = LoggerFactory.getLogger(classOf[ProcessThread])

    override def run(): Unit = {
      while(true) {
        val databusFile = processQueue.take()
        log.info(s"start process #$pId ${databusFile.id}")
        handleJob(
          new URL(modUrl+"/"+databusFile.id.split("/").dropRight(1).mkString("/")),
          new URI(baseUri+"/"+databusFile.id)
        )
        log.info(s"finished process #$pId ${databusFile.id}")
        // TODO delete file
        pId += 1
      }
    }

    def handleJob(modUrl: URL, fileUri: URI): Unit = {

      var wait = true
      var timer = 0.0
      while(wait) {

        val parameters = new ju.HashMap[String,String]
        parameters.put("fileUri",fileUri.toString)

        val con : HttpURLConnection = modUrl.openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("POST")
        con.setReadTimeout(5*60*1000)
        con.setDoOutput(true)

         val out: DataOutputStream = new DataOutputStream(con.getOutputStream)
         out.writeBytes(getParamsString(parameters))
         out.flush()
         out.close()

        con.getResponseCode match {
          case 200 =>
            // TODO logic
            wait = false
          case 202 | 201 =>
            Thread.sleep(200)
            timer += 0.2
            if(timer % 5 == 0) log.info(s"waited ${timer}sec")
          case status =>
            log.warn(s"failed $modUrl  with $fileUri")
            wait = false
        }
      }
    }
    def getParamsString(params: ju.Map[String,String]): String = {
      val result = new StringBuilder()

      import scala.collection.JavaConversions._

      params.entrySet().foreach( entry => {
        result.append(URLEncoder.encode(entry.getKey, "UTF-8"))
        result.append("=")
        result.append(URLEncoder.encode(entry.getValue, "UTF-8"))
        result.append("&")
      })

      val resultString = result.toString()
      if(resultString.length() > 0) {
        resultString.substring(0, resultString.length() - 1)
      } else {
        resultString
      }
    }
  }
}
