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
package org.dbpedia.databus.sink
import java.io.{FileOutputStream, FileWriter}

import better.files.File
import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFFormat}
import org.dbpedia.databus.indexer.Item

class DataidExtSink(val resultDir:String) extends Sink {



  override def consume(output:String) = {
    println(output)
  }

  override def consume(item: Item, model: Model): Unit = {
    val targetDir = File(resultDir)/item.getPath
    targetDir.createDirectoryIfNotExists()
    val targetFile = targetDir/"dataidext.nt"
    this.synchronized{
      val fos = new  FileOutputStream( targetFile.toJava, true)
      RDFDataMgr.write(fos, model, Lang.NTRIPLES) ;
      fos.close()
    }

  }
}
