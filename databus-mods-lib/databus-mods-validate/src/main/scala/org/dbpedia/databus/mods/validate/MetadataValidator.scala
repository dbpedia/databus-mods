//package org.dbpedia.databus.mods.validate
//
//import org.apache.jena.riot.{Lang, RDFDataMgr}
//import org.apache.jena.shacl.lib.ShLib
//import org.apache.jena.shacl.{ShaclValidator, Shapes}
//
//object MetadataValidator extends App {
//
//  scala.util.Properties.setProp("scala.time","true")
//
//  val SHAPES = "dataid-shacl.ttl"
//  val DATA = "dataid-unexpanded.ttl"
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
//}
//
