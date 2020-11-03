package org.dbpedia.databus_mods.server.utils

import java.io.{File, FileInputStream, FileOutputStream}
import java.net.URI
import java.nio.file.{Files, Paths}

import org.apache.jena.graph
import org.apache.jena.graph.NodeFactory
import org.apache.jena.query.DatasetFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.riot.system.{StreamRDF, StreamRDFLib, StreamRDFWrapper}
import org.apache.jena.sparql.core.DatasetGraphFactory
import org.apache.jena.sparql.graph.GraphFactory
import org.dbpedia.databus_mods.server.LinkConfig

/*
                RDFDataMgr.parse(rewritten, new FileInputStream(target.toFile), oldUri.replace(oldBase,"), Lang.TURTLE)

                val jenaModel = ModelFactory.createDefaultModel()
                jenaModel.read(new FileInputStream(link.toFile), null, "TTL")
                VOSUtil.submitToEndpoint(modConfig.name + "/" + id + "/" +target.toFile.getName, jenaModel,
                  config.extServer.sparql.databaseUrl,
                  config.extServer.sparql.databaseUsr,
                  config.extServer.sparql.databasePsw
                )
 */
object BaseUriRewriteStreamWrapper extends App {

  val file = new File("/home/.zhsrc")

  println(file.toURI)
  println(new URI("file",null,"/home/.zshrc",null))

  System.exit(0)

  val sourceBaseDir = new File("/tmp/spo/mounted/")
  val targetBaseDir = new File("/tmp/spo/rewritten/")
  val sourceFile = new File(sourceBaseDir, "ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/mod.ttl")
  val targetFile = new File(targetBaseDir, "ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/mod.ttl")
  targetFile.getParentFile.mkdirs()

  val graph = GraphFactory.createDefaultGraph()

  val rdfStreamWrapper = new BaseUriRewriteStreamWrapper(
    StreamRDFLib.graph(graph),
    "file:///workspace/localRepo",
    "file:///tmp/spo/rewritten",
    sourceBaseDir.getAbsolutePath,
    targetBaseDir.getAbsolutePath,
    Set()
  )
  RDFDataMgr.parse(rdfStreamWrapper, new FileInputStream(sourceFile), Lang.TURTLE)
  RDFDataMgr.write(new FileOutputStream(targetFile), graph, Lang.TURTLE)
}

class BaseUriRewriteStreamWrapper(streamRDF: StreamRDF,
                                  oldBaseUri: String,
                                  newBaseUri: String,
                                  sourceBasePath: String,
                                  targetBasePath: String,
                                  recursiveFileNames: Set[String]
                                 ) extends StreamRDFWrapper(streamRDF) {

  override def triple(triple: graph.Triple): Unit = {

    val isUsed = triple.getPredicate.getURI.equals("http://www.w3.org/ns/prov#generated")

    other.triple(
      graph.Triple.create(
        // Subject is URI
        if (triple.getSubject.isURI && triple.getSubject.getURI.startsWith(oldBaseUri)) {
          NodeFactory.createURI(triple.getSubject.getURI.replace(oldBaseUri, newBaseUri))
          // Subject else
        } else
          triple.getSubject,
        // Predicate
        triple.getPredicate,
        // Object is URI
        if (triple.getObject.isURI && triple.getObject.getURI.startsWith(oldBaseUri)) {
          if (isUsed) {
            println(1)

            val oldObjectUri = triple.getObject.getURI
            val targetPath = Paths.get(oldObjectUri.replace(oldBaseUri, targetBasePath))
            Files.createDirectories(targetPath.getParent)
            val sourcePath = Paths.get(oldObjectUri.replace(oldBaseUri, sourceBasePath))

            if (recursiveFileNames.contains(targetPath.toFile.getName)) {
              val outputStream = new FileOutputStream(targetPath.toFile)
              val rdfStreamWrapper = new BaseUriRewriteStreamWrapper(
                StreamRDFLib.writer(outputStream),
                oldBaseUri,
                newBaseUri,
                sourceBasePath,
                targetBasePath,
                Set()
              )
              RDFDataMgr.parse(rdfStreamWrapper, new FileInputStream(sourcePath.toFile), Lang.TURTLE) //.replace(oldBase, config.extServer.http.volume+s"/${modConfig.name}"), Lang.TURTLE)
            } else {
              println(2)
              Files.copy(sourcePath, targetPath)
            }
            // TODO create symbolic link: targetPath -> sourcePath
          }
          NodeFactory.createURI(triple.getObject.getURI.replace(oldBaseUri, newBaseUri))
          // Object else
        } else
          triple.getObject
      )
    )
  }
}
