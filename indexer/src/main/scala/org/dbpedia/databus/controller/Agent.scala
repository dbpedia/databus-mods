/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.controller

import better.files.File
import org.dbpedia.databus.client.api.DatabusClient
import org.dbpedia.databus.client.filehandling.FileHandler
import org.dbpedia.databus.client.filehandling.download.Downloader
import org.dbpedia.databus.client.sparql.QueryHandler
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.process.Processor
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable.ListBuffer

class Agent ( val datadir : String,   val processors: java.util.List[Processor], val sink:Sink ) extends Serializable {


  def process(item:Item) = {

    // download / check if downloaded
    //TODO Fabian databus client
    //add a dir variable and whatever you need.
    //Note that you can also change the variables in Item or tell me to change them in case you need different fields

    // process and sink
    // TODO Fabian

    val processorsToExecute = getNotYetExecutedProcessors(item, processors)

    val targetDir = File(datadir)
    targetDir.createDirectoryIfNotExists()

    if (!processorsToExecute.isEmpty){
      Downloader.downloadFile(item.downloadURL.toString, item.shaSum, targetDir) match {

        case Some(tempFile:File) =>
          val file = FileHandler.handleFile(tempFile, targetDir, "same", "same").get
          tempFile.delete()

          var i=0
          while (i < processorsToExecute.size()){
            processorsToExecute.get(i).process(file, item, sink)
            i+=1
          }

          file.delete()
          ""


        case None =>
          println("could not process file")
          ""
      }
    }


  }

  /**
    * checks if a processor already processed an item, if yes, the already processed files are not processed again.
    * the method return a list of processor that still need to process this item.
    * That method is for the case, that the whole indexing process interrupted, e.g exceptions
    *
    * @param item Item of a file
    * @param processors processors the user want to make calculation from
    * @return processors that didnt process the file yet
    */
  def getNotYetExecutedProcessors(item:Item, processors:java.util.List[Processor]):java.util.List[Processor]={

    val processorsToExecute = ListBuffer.empty[Processor]

    val queryStr =
      s"""
        |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
        |PREFIX dcat: <http://www.w3.org/ns/dcat#>
        |
        |SELECT ?publisher ?group ?artifact ?version {
        |  ?distribution dcat:downloadURL <${item.downloadURL}> .
        |  ?dataset dcat:distribution ?distribution .
        |  ?dataset dataid:version ?version .
        |  ?dataset a dataid:Dataset ;
        |           dataid:account ?publisher ;
        |           dataid:group ?group ;
        |           dataid:artifact ?artifact ;
        |           dataid:version ?version .
        |}
      """.stripMargin

    val results = QueryHandler.executeQuery(queryStr)
    val result = results.head

    //split the URI at the slashes and take the last cell
    val publisher = result.getResource("?publisher").toString.split("/").last.trim
    val group = result.getResource("?group").toString.split("/").last.trim
    val artifact = result.getResource("?artifact").toString.split("/").last.trim
    val version = result.getResource("?version").toString.split("/").last.trim
    val name = item.downloadURL.toString.split("/").last.trim


    //check if voidProcessor already processed item
    var i=0
    while (i < processors.size()){
      processors.get(i).getClass.getSimpleName match {
        case "VoIDProcessor" => {
          val voidDir = File("voidResults")
          val voidExt = "_VoID.ttl"

          if (!(voidDir / publisher / group/ artifact / version / name.concat(voidExt)).exists) processorsToExecute += processors.get(i)
        }
        case _ => processorsToExecute += processors.get(i)
      }

      i+=1
    }

    import scala.collection.JavaConverters._
    processorsToExecute.toList.asJava
  }

}
