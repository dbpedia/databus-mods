package org.dbpedia.databus_mods.lib.util

import java.io.FileOutputStream
import java.net.{MalformedURLException, URL}

import better.files.File
import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource, ResourceFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus_mods.lib.DatabusModInput

class DatabusModOutputHelper(databusModInput: DatabusModInput, baseUri: String, modName: String, externalResultFile: Option[File] = None) {

  private val model = ModelFactory.createDefaultModel()
  private val modVocabHelper = new DatabusModVocabHelper(modName)

  object Prefixes {
    val prov = "http://www.w3.org/ns/prov#"
    val mod = "http://dataid.dbpedia.org/ns/mod.ttl#"
    val dcat = "http://www.w3.org/ns/dcat#"
    val dataIdMT = "http://dataid.dbpedia.org/ns/mt#"
  }

  private val uriByPrefix: Map[String, String] = Map(
    "mod" -> Prefixes.mod,
    "prov" -> Prefixes.prov,
    "dataid-mt" -> Prefixes.dataIdMT,
    "dcat" -> Prefixes.dcat
  )

  import scala.collection.JavaConverters.mapAsJavaMapConverter
  model.setNsPrefixes(uriByPrefix.asJava)

  private val modURI = s"file://${databusModInput.modMetadataFile(baseUri).parent}"
  private val modResourceURI = s"file://${databusModInput.modMetadataFile(baseUri)}#this"
  private val provFileURI = s"https://databus.dbpedia.org/${databusModInput.id}"
  private val resultURI = externalResultFile match {
    case Some(file) => s"file://${file}#this"
    case None => s"file://${databusModInput.modMetadataFile(baseUri)}#result"
  }


  addStmtToModel(Left(modResourceURI), "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", Left(s"${modURI}/modvocab.ttl#${modName}"))
  addStmtToModel(Left(modResourceURI), s"${Prefixes.prov}used", Left(provFileURI))
  addStmtToModel(Left(modResourceURI), s"${Prefixes.prov}endedAtTime", Left(new XSDDateTime(java.util.Calendar.getInstance())))
  addStmtsResultDerivedFrom(resultURI)


  /**
    * add statement for result to jena model.
    * The result can either be integrated in the modMetaDataFile (modMetaDataFileURI#result),
    * or be written to an external result placed in the same directory as the modMetaDataFile (e.g modResult.ttl)
    *
    * @param resultURI
    * @return
    */
  private def addStmtsResultDerivedFrom(resultURI: String): Unit = {
    addStmtToModel(Left(resultURI), "http://dataid.dbpedia.org/ns/mod.ttl#resultDerivedFrom", Left(s"https://databus.dbpedia.org/${databusModInput.id}"))
    addStmtToModel(Left(modResourceURI), s"${Prefixes.prov}generated", Left(resultURI))
  }


  /**
    * add Statements for mod-generated file to jena model, except external result files!
    * also add definition to vocabulary
    *
    * @param fileName file name of generated file
    * @param label    label of propertyObject
    * @param comment  comment of propertyObject
    */
  def addStmtsForGeneratedFile(fileName: String, label: String = "", comment: String = ""): Unit = {
    addStmtToModel(Left(modResourceURI), s"${Prefixes.prov}generated", Left(s"${modURI}/${fileName}"))

    if (fileName.contains('.')) {
      val fileType = fileName.split('.').last.toLowerCase
      addStmtToModel(Left(s"${modURI}/${fileName}"), s"${Prefixes.mod}${fileType}DerivedFrom", Left(provFileURI))
      if (!(fileType matches ("svg|html"))) modVocabHelper.addFileTypeToModVocab(fileType, label, comment)
    } else {
      addStmtToModel(Left(s"${modURI}/${fileName}"), s"${Prefixes.mod}derivedFrom", Left(provFileURI))
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
    * Right for Jena.Resource and Left for Strings for Subject and any object for Object
    * example: addStmtToModel(Left(modResourceURI), s"${Prefixes.prov}used", Left(provFileURI))
    *
    * @param s subject as resource
    * @param p predicate
    * @param o object
    */
  def addStmtToModel(s: Either[String, Resource], p: String, o: Either[Any, Resource], model: Model = this.model): Unit = {
    model.add(
      ResourceFactory.createStatement(
        if (s.isRight) s.right.get
        else ResourceFactory.createResource(s.left.get),
        ResourceFactory.createProperty(p),
        if (o.isRight) o.right.get
        else {
          try {
            new URL(o.toString)
            ResourceFactory.createResource(o.left.get.toString)
          } catch {
            case malformedURL: MalformedURLException => ResourceFactory.createTypedLiteral(o.left.get)
          }
        })
    )
  }

  /**
    * write out jena model of vocabulary and mod result
    *
    * @param lang desired rdf language
    */
  def writeMetaDataModels(lang: Lang = Lang.TTL): Unit = {
    writeModel(model, databusModInput.modMetadataFile(baseUri), lang)
    writeModel(modVocabHelper.getModel(), File(s"${databusModInput.modMetadataFile(baseUri).parent}/modvocab.ttl"), lang)
  }

  /**
    * write jena model to file
    *
    * @param model jena model
    * @param file  output file
    * @param lang  rdf lang
    */
  def writeModel(model: Model, file: File, lang: Lang = Lang.TTL): Unit = {
    file.parent.createDirectoryIfNotExists()
    val fos = new FileOutputStream(file.toJava, false)
    RDFDataMgr.write(fos, model, lang)
    fos.close()
  }

  /**
    * get URI of result resource
    *
    * @return
    */
  def getResultURI(): String = {
    resultURI
  }

}
