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
package org.dbpedia.databus.process
import java.io._
import java.nio.file.Files

import better.files.File
import org.apache.commons.compress.utils.IOUtils
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink


class MimeTypeProcessor extends Processor {

  val modName = "MimeTypeMod"

  /**
    * Probe mimeType and compression type of a file
    *
    * @param file file to probe
    * @param item file-corresponding item
    * @param sink sink
    */
  override def process(file:File, item: Item, sink: Sink): Unit = {


    val inStream = Compressor.decompress(new BufferedInputStream(new FileInputStream(file.toJava)))

    if(inStream.getClass.getCanonicalName != "java.io.BufferedInputStream") {
      val decompressedFile = file.parent/ s"${file.nameWithoutExtension(includeAll = false)}"
      copyStream(inStream,new FileOutputStream(decompressedFile.toJava))

      val compression = checkMimeType(file)
      val mimetype = checkMimeType(decompressedFile)
      item.mimetype = mimetype

      sink.consume(item, createModel(item, compression, mimetype),modName)
    }
    else {
      val mimetype = checkMimeType(file)
      sink.consume(item, createModel(item, "",mimetype), modName)
    }
  }




  /**
    * Check mimeType of file
    *
    * @param file file to check mimeType from
    * @return mimetype
    */
  def checkMimeType(file: File): String = {
    Files.probeContentType(file.path)
  }

  /**
    * Copy input stream to output stream
    *
    * @param in input stream
    * @param out output stream
    */
  def copyStream(in: InputStream, out: OutputStream): Unit = {
    try {
      IOUtils.copy(in, out)
    }
    finally if (out != null) {
      out.close()
    }
  }


  def createModel(item:Item, compression:String, mimeType:String): Model ={
    val model: Model = ModelFactory.createDefaultModel()

    val prefixMap: Map[String, String] = Map(
      "myMod" -> "http://myservice.org/mimeType/repo/",
      "myModVoc" -> "http://myservice.org/mimeType/repo/modvocab.ttl#",
      "dcat" -> "http://www.w3.org/ns/dcat#",
      "dataid-mt" -> "http://dataid.dbpedia.org/ns/mt#"
    )

    import scala.collection.JavaConverters.mapAsJavaMapConverter
    model.setNsPrefixes(prefixMap.asJava)

    val resultURI = s"${prefixMap("myMod")}${item.shaSum}.ttl#result"

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(resultURI),
        ResourceFactory.createProperty("http://dataid.dbpedia.org/ns/mod.ttl#resultDerivedFrom"),
        ResourceFactory.createResource(item.file.toString)))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(resultURI),
        ResourceFactory.createProperty("http://www.w3.org/ns/dcat#mediaType"),
        ResourceFactory.createResource(s"http://dataid.dbpedia.org/ns/mt#${mimeType}")))

    if (compression.nonEmpty) {
      model.add(
        ResourceFactory.createStatement(
          ResourceFactory.createResource(resultURI),
          ResourceFactory.createProperty("http://www.w3.org/ns/dcat#compression"),
          ResourceFactory.createResource(s"http://dataid.dbpedia.org/ns/mt#${compression}")))

    }

    model
  }
}
