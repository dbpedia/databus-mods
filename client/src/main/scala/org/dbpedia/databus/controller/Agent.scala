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

import java.io.ObjectStreamClass

import better.files.File
import org.dbpedia.databus.client.filehandling.FileHandler
import org.dbpedia.databus.client.filehandling.download.Downloader
import org.dbpedia.databus.indexer.{Index, Item}
import org.dbpedia.databus.mod.process.Processor
import org.dbpedia.databus.sink.Sink

import scala.collection.mutable.ListBuffer

class Agent ( val datadir : String,   val processors: java.util.List[Processor], val sink:Sink ) extends Serializable {

  val targetDir = File(datadir)
  targetDir.createDirectoryIfNotExists()

  /**
    * download dataset and execute processors on it
    *
    * @param item dataset to do calculations on
    * @param index Indexer, to access derby db
    * @return
    */
  def process(item:Item, index:Index) = {

    // process and sink

    val alreadyProcessed = index.getStatuses(item.shaSum)
    val processorsToExecute = new ListBuffer[Processor]

    //check if any of the desired processors already made calculations on item
    var i=0
    while (i < processors.size()){
      val processor= processors.get(i)
      if (!alreadyProcessed.contains(getProcessorUIDString(processor))) processorsToExecute += processor
      i+=1
    }

    if (processorsToExecute.nonEmpty){

      Downloader.downloadFile(item.downloadURL.toString, item.shaSum, targetDir) match {

        case Some(tempFile:File) =>
          val file = FileHandler.handleFile(tempFile, targetDir, "same", "same").get
          tempFile.delete()

          var i=0
          while (i < processorsToExecute.size){

            val processor = processorsToExecute(i)
            processor.process(file, item, sink)
            index.setStatusProcessed(item.shaSum, getProcessorUIDString(processor))

            i+=1
          }

          file.delete()
          ""

        case None =>
          println("could not download file");""
      }
    } else println(s"all desired processors have made calculations on ${item.file} already.");""


  }

  def getProcessorUIDString(processor:Processor):String={
    val serialVersionID = ObjectStreamClass.lookup(processor.getClass).getSerialVersionUID.toString.replace("|", "\\|")
    s"${processor.getClass.getCanonicalName.replace("|", "\\|")}|$serialVersionID"
  }

}
