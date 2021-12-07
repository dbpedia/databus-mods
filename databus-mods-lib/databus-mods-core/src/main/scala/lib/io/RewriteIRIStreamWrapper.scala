package lib.io

import org.apache.jena.graph
import org.apache.jena.graph.{Node, NodeFactory}
import org.apache.jena.riot.system.{StreamRDF, StreamRDFWrapper}

class RewriteIRIStreamWrapper(streamRDF: StreamRDF,
                              regex: String,
                              replacement: String) extends StreamRDFWrapper(streamRDF) {

  override def triple(triple: graph.Triple): Unit = {
    other.triple(
      graph.Triple.create(
        rewriteIRI(triple.getSubject),
        rewriteIRI(triple.getPredicate),
        rewriteIRI(triple.getObject)))
  }

  private def rewriteIRI(node: Node): Node = {
    if (node.isURI) {
      NodeFactory.createURI(node.getURI.replaceAll(regex, replacement))
    } else {
      node
    }
  }
}
