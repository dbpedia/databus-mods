package org.dbpedia.databus_mods.server.core.utils

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr, RDFLanguages}
import virtuoso.jena.driver.VirtDataset

object VOSUtil extends App {

  val ttl =
    """<http://subject.org>  <http://property.org>
      |                "foobar2" .""".stripMargin

  val model = ModelFactory.createDefaultModel()

  RDFDataMgr.read(model, new ByteArrayInputStream(ttl.getBytes(StandardCharsets.UTF_8)),RDFLanguages.NTRIPLES)

  model.write(System.out,Lang.NTRIPLES.getName)

  val virtDataset = new VirtDataset("jdbc:virtuoso://localhost:32972/charset=UTF-8/","dba","myDbaPassword")

  println(virtDataset.containsNamedModel("http://localhost:8890/test"))

  virtDataset.removeNamedModel("http://localhost:8890/test")

  virtDataset.addNamedModel("http://localhost:8890/test",model,false)
  virtDataset.commit()
  virtDataset.close()
}
