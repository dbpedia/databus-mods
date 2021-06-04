package org.dbpedia.databus_mods.lib.util

import java.net.{MalformedURLException, URL}

import org.apache.jena.rdf.model.{Model, Resource, ResourceFactory}

object ModelUtil {

  sealed trait SimpleNodeWrapper

  final case class SimpleResourceWrapper(resource: Resource) extends SimpleNodeWrapper

  final case class SimpleStringWrapper(str: String) extends SimpleNodeWrapper

  final case class SimpleAnyWrapper(any: Any) extends SimpleNodeWrapper

  implicit def stringToSimpleString(str: String): SimpleStringWrapper = SimpleStringWrapper(str)

  implicit def resourceToSimpleResource(resource: Resource): SimpleResourceWrapper = SimpleResourceWrapper(resource)

  implicit def anyToSimpleAny(any: Any): SimpleAnyWrapper = SimpleAnyWrapper(any)

  implicit class ModelWrapper(model: Model) {

    def addStmtToModel(s: SimpleNodeWrapper, p: String, o: SimpleNodeWrapper, model: Model = this.model): Unit = {
      model.add(
        ResourceFactory.createStatement(
          // subject
          s match {
            case SimpleResourceWrapper(resource) => resource
            case SimpleStringWrapper(str) => ResourceFactory.createResource(str)
          },
          // property
          ResourceFactory.createProperty(p),
          // object
          o match {
            case SimpleResourceWrapper(resource) => resource
            case SimpleStringWrapper(str) => ResourceFactory.createResource(str)
            case SimpleAnyWrapper(any) =>
              try {
                new URL(any.toString)
                ResourceFactory.createResource(any.toString)
              } catch {
                case malformedURL: MalformedURLException => ResourceFactory.createTypedLiteral(any)
              }
          }
        )
      )
    }
  }

}
