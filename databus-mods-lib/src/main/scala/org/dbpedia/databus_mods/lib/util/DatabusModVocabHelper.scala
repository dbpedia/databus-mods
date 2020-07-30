package org.dbpedia.databus_mods.lib.util

import java.net.{MalformedURLException, URL}

import com.sun.xml.internal.bind.v2.TODO
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}

class DatabusModVocabHelper(modName: String) {

  private val modVocabModel = ModelFactory.createDefaultModel()

  object Prefixes {
    val owl = "http://www.w3.org/2002/07/owl#"
    val mod = "http://dataid.dbpedia.org/ns/mod.ttl#"
    val rdfs = "http://www.w3.org/2000/01/rdf-schema#"
    val prov = "http://www.w3.org/ns/prov#"
  }

  private val uriByPrefix: Map[String, String] = Map(
    "rdfs" -> Prefixes.rdfs,
    "mod" -> Prefixes.mod,
    "owl" -> Prefixes.owl,
    "prov" -> Prefixes.prov
  )

  import scala.collection.JavaConverters.mapAsJavaMapConverter

  modVocabModel.setNsPrefixes(uriByPrefix.asJava)

  addStmtToModVocab(s"#${modName}", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", s"${Prefixes.owl}Class")
  addStmtToModVocab(s"#${modName}", s"${Prefixes.rdfs}subClassOf", s"${Prefixes.mod}DatabusMod")
  addStmtToModVocab(s"#${modName}", s"${Prefixes.rdfs}label", "")

  /**
    * add statement to mod vocabulary model
    *
    * @param s subject
    * @param p predicate
    * @param o object
    */
  private def addStmtToModVocab(s: String, p: String, o: Object): Unit = {
    modVocabModel.add(
      ResourceFactory.createStatement(
        ResourceFactory.createResource(s),
        ResourceFactory.createProperty(p),
        try {
          new URL(o.toString)
          ResourceFactory.createResource(o.toString)
        } catch {
          case malformedURL: MalformedURLException => ResourceFactory.createTypedLiteral(o)
        })
    )
  }

  def addFileTypeToModVocab(fileType: String, label: String, comment: String): Unit = {
    val subj = s"${Prefixes.mod}${fileType.toLowerCase}DerivedFrom"
    addStmtToModVocab(subj, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", s"${Prefixes.owl}ObjectProperty")
    addStmtToModVocab(subj, s"${Prefixes.rdfs}subPropertyOf", s"${Prefixes.prov}wasDerivedFrom")
    addStmtToModVocab(subj, s"${Prefixes.rdfs}subPropertyOf", s"${Prefixes.mod}wasDerivedFrom")
    addStmtToModVocab(subj, s"${Prefixes.rdfs}domain", s"${Prefixes.mod}${fileType.toUpperCase}_Summary")
    addStmtToModVocab(subj, s"${Prefixes.rdfs}label", label)
    addStmtToModVocab(subj, s"${Prefixes.rdfs}comment", comment)
  }


  /**
    * get model of ModVocabHelper
    *
    * @return model
    */
  def getModel(): Model = {
    modVocabModel
  }

}
