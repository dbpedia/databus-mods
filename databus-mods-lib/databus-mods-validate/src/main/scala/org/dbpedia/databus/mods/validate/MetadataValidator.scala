package org.dbpedia.databus.mods.validate

import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.shacl.lib.ShLib
import org.apache.jena.shacl.{ShaclValidator, Shapes}
import picocli.CommandLine.Command

import java.util.concurrent.Callable

@Command(name = "metadata", mixinStandardHelpOptions = true)
class MetadataValidator extends Callable[Int] {

  scala.util.Properties.setProp("scala.time","true")

//  val SHAPES = null
//  val DATA = null
//
//  val shapesGraph = RDFDataMgr.loadGraph(SHAPES)
//  val dataGraph = RDFDataMgr.loadGraph(DATA)
//
//  val shapes = Shapes.parse(shapesGraph)
//
//  val report = ShaclValidator.get.validate(shapes,dataGraph)
//  ShLib.printReport(report)
//
//  RDFDataMgr.write(System.out,report.getModel,Lang.TTL)

  override def call(): Int = {
    0 // SUCCESS
  }
}

