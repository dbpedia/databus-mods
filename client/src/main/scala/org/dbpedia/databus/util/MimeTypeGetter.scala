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
package org.dbpedia.databus.util

import java.net.URL

import org.apache.jena.riot.Lang

import scala.collection.mutable

object MimeTypeGetter {

  def getRDFFormat(url:URL): Lang= {

    val querystr =
      s"""
         |PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
         |PREFIX dcat: <http://www.w3.org/ns/dcat#>
         |
        |SELECT DISTINCT ?type
         |WHERE {
         |  ?distribution dcat:mediaType ?type .
         |  ?distribution dcat:downloadURL <$url> .
         |}
      """.stripMargin


    val mimeTypeRDFMap:mutable.HashMap[String,Lang] = mutable.HashMap.empty
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationNTriples",Lang.NT)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#TextTurtle", Lang.TTL)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationRDFXML", Lang.RDFXML)
    mimeTypeRDFMap.put("http://dataid.dbpedia.org/ns/mt#ApplicationJson", Lang.JSONLD)

    val result = org.dbpedia.databus.client.sparql.QueryHandler
      .executeQuery(querystr)
      .head
      .getResource("?type")
      .toString

    mimeTypeRDFMap.getOrElse(result, Lang.TTL)
  }


}
