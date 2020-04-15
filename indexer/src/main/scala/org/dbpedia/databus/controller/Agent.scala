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
import org.dbpedia.databus.filehandling.FileUtil
import org.dbpedia.databus.filehandling.download.Downloader
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.process.Processor
import org.dbpedia.databus.sink.Sink

class Agent ( val processors: java.util.List[Processor], val sink:Sink ) {


  def process(item:Item) = {

    // download / check if downloaded
    //TODO Fabian databus client
    //add a dir variable and whatever you need.
    //Note that you can also change the variables in Item or tell me to change them in case you need different fields

    // process and sink
    // TODO Fabian

    val tempDir = File("./temp")
    tempDir.createDirectoryIfNotExists()

    val file = tempDir/"tempFile"

    (0 to 4).iterator
      .takeWhile(_ => !FileUtil.checkSum(file, item.shaSum))
      .foreach(i => Downloader.downloadUrlToFile(item.downloadURL, file))

    // Processor.scala needs to extended, as def process method needs more variables, such as the filename, etc.
    var i=0
    while (i < processors.size()){
      processors.get(i).process(file, item, sink)
      i+=1
    }

    file.delete()

  }

}
