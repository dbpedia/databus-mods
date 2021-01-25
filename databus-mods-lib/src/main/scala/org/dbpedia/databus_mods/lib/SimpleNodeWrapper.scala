//package org.dbpedia.databus_mods.lib
//
//import org.apache.jena.rdf.model.Resource
//
//sealed trait SimpleNodeWrapper
//
//final case class SimpleResourceWrapper(resource: Resource) extends SimpleNodeWrapper
//
//final case class SimpleStringWrapper(str: String) extends SimpleNodeWrapper
//
//final case class SimpleAnyWrapper(any: Any) extends SimpleNodeWrapper
//
//object SimpleNodeWrapper {
//
//  object implicits {
//    implicit def stringToSimpleString(str: String): SimpleStringWrapper = SimpleStringWrapper(str)
//
//    implicit def resourceToSimpleResource(resource: Resource): SimpleResourceWrapper = SimpleResourceWrapper(resource)
//
//    implicit def anyToSimpleAny(any: Any): SimpleAnyWrapper = SimpleAnyWrapper(any)
//  }
//}
