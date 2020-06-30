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

import java.io.FileOutputStream

import better.files.File
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus.indexer.Item

class RDFSink(val resultDir: String) extends Sink {


  override def consume(output: String) = {
    println(output)
  }

  override def consume(item: Item, model: Model, modName:String): Unit = {

    val targetDir = File(resultDir) / item.getPath
    targetDir.createDirectoryIfNotExists()

    val targetFile = targetDir / s"${item.shaSum}.ttl"

    this.synchronized {
      val fos = new FileOutputStream(targetFile.toJava, false)
      RDFDataMgr.write(fos, prepareModel(model, item, modName), Lang.TTL)
      fos.close()
    }

  }

  def prepareModel(model:Model, item:Item, modName:String)={

    import scala.collection.JavaConverters.mapAsJavaMapConverter

    val prefixMap: Map[String, String] = Map(
      "mod" -> "http://dataid.dbpedia.org/ns/mod.ttl#",
      "prov" -> "http://www.w3.org/ns/prov#",
      "dataid-mt" -> "http://dataid.dbpedia.org/ns/mt#",
      "dcat" -> "http://www.w3.org/ns/dcat#"
    )

    model.setNsPrefixes(prefixMap.asJava)

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.svg"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}svgDerivedFrom"),
        ResourceFactory.createResource(item.file.toString)))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.html"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}htmlDerivedFrom"),
        ResourceFactory.createResource(item.file.toString)))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.ttl#this"),
        ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myModVoc")}$modName")))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.ttl#this"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}used"),
        ResourceFactory.createResource(item.file.toString)))

    model.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.ttl#this"),
        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}endedAtTime"),
        ResourceFactory.createTypedLiteral(java.time.ZonedDateTime.now.toString, XSDDatatype.XSDdateTime)))

    val stmt =  ResourceFactory.createStatement(
      ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.ttl#this"),
      ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}generated"),
      ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.html"))

    model.add(stmt)

    model.add(
      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.svg")))

    model.add(
      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${item.shaSum}.ttl#result")))
  }
}
