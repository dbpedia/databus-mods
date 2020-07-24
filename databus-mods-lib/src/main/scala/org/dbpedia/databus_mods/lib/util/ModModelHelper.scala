package org.dbpedia.databus_mods.lib.util

import java.io.FileOutputStream
import java.net.{MalformedURLException, URL}

import better.files.File
import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory, ResourceFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus_mods.lib.DatabusModInput

class ModModelHelper(databusModInput: DatabusModInput, baseUri: String, modName: String) {

  //  val subject: String = databusModInput.modMetadataFile.pathAsString //  private vocabModel =  private implicit val baseDir: String = baseUri
  private val model = ModelFactory.createDefaultModel()
  private val modVocabHelper = new ModVocabHelper(modName)

  object Prefixes {
    val prov = "http://www.w3.org/ns/prov#"
    val mod = "http://dataid.dbpedia.org/ns/mod.ttl#"
  }

  private val uriByPrefix: Map[String, String] = Map(
    "mod" -> Prefixes.mod,
    "prov" -> Prefixes.prov
  )

  import scala.collection.JavaConverters.mapAsJavaMapConverter

  model.setNsPrefixes(uriByPrefix.asJava)

  private val modURI = s"file://${databusModInput.modMetadataFile(baseUri).parent}"
  private val modResourceURI = s"file://${databusModInput.modMetadataFile(baseUri)}#this"
  private val provFileURI = s"https://databus.dbpedia.org/${databusModInput.id}"

  addStmtToModel(modResourceURI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", s"${modURI}/modvocab.ttl#${modName}")
  addStmtToModel(modResourceURI, s"${Prefixes.prov}used", provFileURI)
  addStmtToModel(modResourceURI, s"${Prefixes.prov}endedAtTime", new XSDDateTime(java.util.Calendar.getInstance()))

  /**
    * add statement for result to jena model.
    * The result can either be integrated in the modMetaDataFile (modMetaDataFileURI#result),
    * or be written to an external result placed in the same directory as the modMetaDataFile (e.g modResult.ttl)
    *
    * @param resultURI
    * @return
    */
  def generateResultDerivedFrom(resultURI: String): Unit = {
    addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/mod.ttl#resultDerivedFrom", s"https://databus.dbpedia.org/${databusModInput.id}")
    addStmtToModel(modResourceURI, s"${Prefixes.prov}generated", resultURI)
  }

  /**
    * add Statements for mod-generated file to jena model, except external result files!
    *
    * @param fileName file name of generated file
    */
  def addStmtsForGeneratedFile(fileName: String): Unit = {
    addStmtToModel(modResourceURI, s"${Prefixes.prov}generated", s"${modURI}/${fileName}")

    if (fileName.contains('.')) {
      addStmtToModel(s"${modURI}/${fileName}", s"${Prefixes.mod}${fileName.split('.').last.toLowerCase}DerivedFrom", provFileURI)
    } else {
      addStmtToModel(s"${modURI}/${fileName}", s"${Prefixes.mod}derivedFrom", provFileURI)
    }
  }

  /**
    * add Prefix to Jena Model
    *
    * @param prefix prefix name
    * @param url    url to be prefixed
    * @return
    */
  def addPrefix(prefix: String, url: URL): Unit = {
    model.setNsPrefix(prefix, url.toString)
  }


  /**
    * add statement to jena model
    *
    * @param s subject
    * @param p predicate
    * @param o object
    */
  def addStmtToModel(s: String, p: String, o: Object): Unit = {
    model.add(
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

  /**
    * write out jena model
    *
    * @param lang desired rdf language
    */
  def writeModel(lang: Lang = Lang.TTL): Unit = {
    val fos = new FileOutputStream(databusModInput.modMetadataFile(baseUri).toJava, false)
    RDFDataMgr.write(fos, model, lang)
    fos.close()

    val fosModVocab = new FileOutputStream(File(s"${databusModInput.modMetadataFile(baseUri).parent}/modvocab.ttl").toJava, false)
    RDFDataMgr.write(fosModVocab, modVocabHelper.getModel(), Lang.TTL)
    fos.close()
  }

  //  def addModInformationToModel(model: Model, databusModInput: DatabusModInput, modName: String): Unit = {
  //
  //    import scala.collection.JavaConverters.mapAsJavaMapConverter
  //
  //    val prefixMap: Map[String, String] = Map(
  //      "mod" -> "http://dataid.dbpedia.org/ns/mod.ttl#",
  //      "prov" -> "http://www.w3.org/ns/prov#",
  //      "dataid-mt" -> "http://dataid.dbpedia.org/ns/mt#",
  //      "dcat" -> "http://www.w3.org/ns/dcat#"
  //    )
  //
  //    model.setNsPrefixes(prefixMap.asJava)
  //
  //    val provFileResource = ResourceFactory.createResource(s"https://databus.dbpedia.org/${databusModInput.id}")
  //    val modResource = ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.ttl#this")
  //
  //    model.add(
  //      ResourceFactory.createStatement(
  //        modResource,
  //        ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
  //        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myModVoc")}$modName")))
  //
  //    model.add(
  //      ResourceFactory.createStatement(
  //        modResource,
  //        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}used"),
  //        provFileResource))
  //
  //    model.add(
  //      ResourceFactory.createStatement(
  //        modResource,
  //        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}endedAtTime"),
  //        ResourceFactory.createTypedLiteral(java.time.ZonedDateTime.now.toString, XSDDatatype.XSDdateTime)))
  //
  //    model.add(
  //      ResourceFactory.createStatement(
  //        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.svg"),
  //        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}svgDerivedFrom"),
  //        provFileResource))
  //
  //    model.add(
  //      ResourceFactory.createStatement(
  //        ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.html"),
  //        ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("mod")}htmlDerivedFrom"),
  //        provFileResource))
  //
  //    val stmt = ResourceFactory.createStatement(
  //      modResource,
  //      ResourceFactory.createProperty(s"${model.getNsPrefixMap.get("prov")}generated"),
  //      ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.html"))
  //
  //    model.add(stmt)
  //
  //    model.add(
  //      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.svg")))
  //
  //    model.add(
  //      stmt.changeObject(ResourceFactory.createResource(s"${model.getNsPrefixMap.get("myMod")}${databusModInput.id}/mod.ttl#result")))
  //  }


  //  databusModInput.modResourceFile("") as fileName
  //
  //  def generatesProvDerived(fileName: String, property: String): Unit = { //    val stmt = ResourceFactory.createStatement(
  //    //      ResourceFactory.createResource(subject),
  //    //      ResourceFactory.createProperty(property),
  //    //      provFileResource
  //    //    )
  //    //    )
  //  }

}

class ModVocabHelper(modName: String) {


  private val modVocabModel = ModelFactory.createDefaultModel()

  object Prefixes {
    val owl = "http://www.w3.org/2002/07/owl#"
    val mod = "http://dataid.dbpedia.org/ns/mod.ttl#"
    val rdfs = "http://www.w3.org/2000/01/rdf-schema#"
  }

  private val uriByPrefix: Map[String, String] = Map(
    "rdfs" -> Prefixes.rdfs,
    "mod" -> Prefixes.mod,
    "owl" -> Prefixes.owl
  )

  import scala.collection.JavaConverters.mapAsJavaMapConverter

  modVocabModel.setNsPrefixes(uriByPrefix.asJava)


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

  addStmtToModVocab(s"#${modName}", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", s"${Prefixes.owl}Class")
  addStmtToModVocab(s"#${modName}", s"${Prefixes.rdfs}subClassOf", s"${Prefixes.mod}DatabusMod")
  addStmtToModVocab(s"#${modName}", s"${Prefixes.rdfs}label", "")

  def getModel(): Model = {
    modVocabModel
  }
}