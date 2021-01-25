package org.dbpedia.databus_mods.server.core.io.rdf

import java.io.{FileInputStream, FileOutputStream}
import java.net.URI
import java.nio.file.{Files, Paths}

import org.apache.jena.graph
import org.apache.jena.graph.NodeFactory
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.apache.jena.riot.system.{StreamRDF, StreamRDFLib, StreamRDFWrapper}
import org.dbpedia.databus_mods.server.core.LinkConfig
import org.dbpedia.databus_mods.server.core.{Config,ModConfig}
import org.dbpedia.databus_mods.server.core.utils.VOSUtil

class BaseRewriteStreamWrapper(streamRDF: StreamRDF,
                               id: String,
                               oldBase: String,
                               newBase: String,
                               config: Config,
                               modConfig: ModConfig,
                               linkConfig: LinkConfig,
                               recursive: Boolean = false
                              ) extends StreamRDFWrapper(streamRDF) {

  override def triple(triple: graph.Triple): Unit = {

    val isUsed = triple.getPredicate.getURI.equals("http://www.w3.org/ns/prov#generated")

    other.triple(
      graph.Triple.create(
        if (triple.getSubject.isURI && triple.getSubject.getURI.startsWith(oldBase))
          NodeFactory.createURI(triple.getSubject.getURI.replace(oldBase, newBase))
        else
          triple.getSubject,
        triple.getPredicate,
        if (triple.getObject.isURI && triple.getObject.getURI.startsWith(oldBase)) {
          if (isUsed && recursive) {
            val oldUri = triple.getObject.getURI
            val link = Paths.get(new URI(oldUri.replace(linkConfig.localRepo, config.provider.http.volume+s"/${modConfig.name}")))
            val target = Paths.get(new URI(oldUri.replace(linkConfig.localRepo, linkConfig.mountRepo)))
            Files.createDirectories(link.getParent)
            if (!Files.exists(link)) {
              if(modConfig.load.contains(target.toFile.getName)) {
                val outputStream = new FileOutputStream(link.toFile)
                val rewritten = new BaseRewriteStreamWrapper(StreamRDFLib.writer(outputStream), id, oldBase, newBase, config, modConfig, linkConfig)
                RDFDataMgr.parse(rewritten, new FileInputStream(target.toFile), oldUri.replace(oldBase, config.provider.http.volume+s"/${modConfig.name}"), Lang.TURTLE)


                val jenaModel = ModelFactory.createDefaultModel()
                jenaModel.read(new FileInputStream(link.toFile), null, "TTL")
//                VOSUtil.submitToEndpoint(modConfig.name + "/" + id + "/" +target.toFile.getName, jenaModel,
//                  config.provider.sparql.databaseUrl,
//                  config.provider.sparql.databaseUsr,
//                  config.provider.sparql.databasePsw
//                )
              } else {
                Files.copy(target, link)
              }
            }
            // TODO
            //  Files.createSymbolicLink(link,target)
          }
          NodeFactory.createURI(triple.getObject.getURI.replace(oldBase, newBase))
        } else
          triple.getObject
      )
    )
  }
}
