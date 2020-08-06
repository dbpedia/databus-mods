package org.dbpedia.databus_mods.mimetype

import java.io._
import java.util.Calendar

import better.files.File
import org.apache.any23.mime.TikaMIMETypeDetector
import org.apache.commons.io.IOUtils
import org.apache.jena.query.{QueryExecutionFactory, QueryFactory}
import org.apache.jena.rdf.model.Resource
import org.apache.jena.riot.RDFDataMgr
import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
import org.dbpedia.databus_mods.lib.{AbstractDatabusModExecutor, DatabusModInput}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DatabusModExecutor @Autowired()(config: Config) extends AbstractDatabusModExecutor {

  private val modName = "MimeTypeMod"
  private val log = LoggerFactory.getLogger(classOf[DatabusModExecutor])

  private implicit val basePath: String = config.volumes.localRepo

  private val ianaOntology = RDFDataMgr.loadModel("http://dataid.dbpedia.org/iana/ianaOntology.ttl")

  /**
    * Probe mimeType and compression type of a file
    *
    * @param databusModInput mod input data
    */
  def process(databusModInput: DatabusModInput): Unit = {

    try {
      log.info(s"process ${databusModInput.id}")
      val metadataFile = databusModInput.modMetadataFile
      metadataFile.parent.createDirectories()

      val inputFile = databusModInput.file
      val inStream = Compressor.decompress(new BufferedInputStream(new FileInputStream(inputFile.toJava)))

      if (inStream.getClass.getCanonicalName != "java.io.BufferedInputStream") {
        val decompressedFile = inputFile.parent / s"${inputFile.nameWithoutExtension(includeAll = false)}"
        copyStream(inStream, new FileOutputStream(decompressedFile.toJava))

        val compression = checkMimeType(inputFile)
        val mimeType = checkMimeType(decompressedFile)

        decompressedFile.delete()
        writeResultsToFiles(databusModInput, compression, mimeType)
      }
      else {
        val mimetype = checkMimeType(inputFile)
        writeResultsToFiles(databusModInput, "", mimetype)
      }

//      addModInformationToModel(resultModel, databusModInput, "MimeTypeMod")
    }
    catch {
      case e: Exception =>
        log.error(s"failed to process ${databusModInput.id}")
        databusModInput.modErrorFile.write(
          Calendar.getInstance().getTime.toString + "\n" +
            e.getStackTrace.mkString("\n")
        )
    }
  }

  /**
    * Copy input stream to output stream
    *
    * @param in  input stream
    * @param out output stream
    */
  def copyStream(in: InputStream, out: OutputStream): Unit = {
    try {
      IOUtils.copy(in, out)
    }
    finally if (out != null) {
      out.close()
    }
  }

  /**
    * Check mimeType of file
    *
    * @param file file to check mimeType from
    * @return mimetype
    */
  def checkMimeType(file: File): String = {
    //  Files.probeContentType(file.path)
    val detector = new TikaMIMETypeDetector()
    detector.guessMIMEType(file.name, new BufferedInputStream(new FileInputStream(file.toJava)), null).toString
  }


  /**
    * write result into jena model
    *
    * @param databusModInput input data on which calculations were carried out
    * @param compression     compression of file
    * @param mimeType        mime type of file
    * @return modModelHelper
    */
  def writeResultsToFiles(databusModInput: DatabusModInput, compression: String, mimeType: String): Unit = {

    val modelHelper = new org.dbpedia.databus_mods.lib.util.DatabusModOutputHelper(databusModInput, config.volumes.localRepo, modName)
    val resultURI = modelHelper.getResultURI()

    modelHelper.addStmtToModel(Left(resultURI), "http://www.w3.org/ns/dcat#mediaType", Left(getMimeTypeFromIanaOntology(mimeType)))

    if (compression.nonEmpty) {
      modelHelper.addStmtToModel(Left(resultURI), "http://www.w3.org/ns/dcat#compression", Left(s"http://dataid.dbpedia.org/ns/mt#$compression"))
    }

    //        modelHelper.addStmtsForGeneratedFile("mod.png")
    //        modelHelper.addStmtsForGeneratedFile("mod.svg")


    modelHelper.writeMetaDataModels()
  }

  /**
    * get MimeType URI of Iana Ontology that corresponds to calculated mimeType
    *
    * @param mimeType calculated mimeType
    * @return Resource of ianaOntology that matches with the mimeType
    */
  def getMimeTypeFromIanaOntology(mimeType: String): Resource = {
    val queryStr =
      s"""
         |SELECT ?s
         |WHERE {
         | ?s ?p ?o .
         | FILTER (regex(str(?s), '$mimeType','i'))
         |}
         |LIMIT 1
      """.stripMargin

    val query = QueryFactory.create(queryStr)
    val qe = QueryExecutionFactory.create(query, ianaOntology)

    val results = qe.execSelect()
    val result = results.next().getResource("s")
    qe.close()

    result
  }

}