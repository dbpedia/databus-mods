package org.dbpedia.databus_mods.lib.util

import java.io.FileOutputStream
import java.net.{MalformedURLException, URL}

import better.files.File
import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource, ResourceFactory}
import org.apache.jena.riot.{Lang, RDFDataMgr}
import org.dbpedia.databus_mods.lib.DatabusModInput

sealed trait SimpleNodeWrapper

final case class SimpleResourceWrapper(resource: Resource) extends SimpleNodeWrapper

final case class SimpleStringWrapper(str: String) extends SimpleNodeWrapper

final case class SimpleAnyWrapper(any: Any) extends SimpleNodeWrapper

object SimpleNodeWrapper {

  object implicits {
    implicit def stringToSimpleString(str: String): SimpleStringWrapper = SimpleStringWrapper(str)

    implicit def resourceToSimpleResource(resource: Resource): SimpleResourceWrapper = SimpleResourceWrapper(resource)

    implicit def anyToSimpleAny(any: Any): SimpleAnyWrapper = SimpleAnyWrapper(any)
  }

}

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
    case Some(file) => {
      if (externalResultFile.get.extension(false) == "ttl") s"file://$file#this"
      else s"file://$file"
    }
    case None => s"file://${databusModInput.modMetadataFile(baseUri)}#result"
  }

  import SimpleNodeWrapper.implicits._

  addStmtToModel(modResourceURI, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type", s"${modURI}/modvocab.ttl#${modName}")
  addStmtToModel(modResourceURI, s"${Prefixes.prov}used", provFileURI)
  addStmtToModel(modResourceURI, s"${Prefixes.prov}endedAtTime", new XSDDateTime(java.util.Calendar.getInstance()))
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
    addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/mod.ttl#resultDerivedFrom", s"https://databus.dbpedia.org/${databusModInput.id}")
    addStmtToModel(modResourceURI, s"${Prefixes.prov}generated", resultURI)
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
    addStmtToModel(modResourceURI, s"${Prefixes.prov}generated", s"$modURI/$fileName")

    if (fileName.contains('.')) {
      val fileType = fileName.split('.').last.toLowerCase
      addStmtToModel(s"$modURI/$fileName", s"${Prefixes.mod}${fileType}DerivedFrom", provFileURI)
      if (!(fileType matches "svg|html")) modVocabHelper.addFileTypeToModVocab(fileType, label, comment)
    } else {
      addStmtToModel(s"$modURI/$fileName", s"${Prefixes.mod}derivedFrom", provFileURI)
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
