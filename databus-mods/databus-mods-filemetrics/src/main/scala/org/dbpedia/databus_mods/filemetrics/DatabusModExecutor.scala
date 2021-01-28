//package org.dbpedia.databus_mods.filemetrics
//
//import java.io._
//import java.util.Calendar
//
//import better.files.File
//import org.apache.commons.compress.archivers.{ArchiveException, ArchiveInputStream, ArchiveStreamFactory}
//import org.apache.commons.compress.compressors.{CompressorException, CompressorInputStream, CompressorStreamFactory}
//import org.apache.jena.rdf.model.ModelFactory
//import org.dbpedia.databus_mods.lib.DatabusModInput
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//import scala.io.{Codec, Source}
//
//@Service
//class DatabusModExecutor @Autowired()(config: Config) extends AbstractDatabusModExecutor {
//
//  private val modName = "FileMetricsMod"
//  private val log = LoggerFactory.getLogger(classOf[DatabusModExecutor])
//
//  private implicit val basePath: String = config.volumes.localRepo
//
//  /**
//    * calculate some metrics of a file:
//    * - non-empty lines
//    * - duplicates
//    * - sorted?
//    * - uncompressed byte size
//    *
//    * @param databusModInput mod input data
//    */
//  def process(databusModInput: DatabusModInput): Unit = {
//
//    try {
//      log.info(s"process ${databusModInput.id}")
//      val metadataFile = databusModInput.modMetadataFile
//      metadataFile.parent.createDirectories()
//
//      var nonEmptyLines: Long = 0L
//      var duplicates: Long = 0L
//      var sorted: Boolean = false
//      var uncompressedByteSize: Long = 0L
//
//      try {
//        var nonEmpty = 0L
//        var dupes = 0L
//        var sort: Boolean = true
//        var charSize = 0L
//        var uncompressedSize = 0L
//
//        var previousLine: String = null
//        try {
//          val in = getInputStream(databusModInput.file)
//
//          val it: Iterator[String] = Source.fromInputStream(in)(Codec.UTF8).getLines()
//          while (it.hasNext) {
//            val line = it.next()
//            charSize += line.length //now counts the number of chars (not including linefeeds)
//
//            // non empty lines
//            if (!line.trim.isEmpty) {
//              nonEmpty += 1
//            }
//
//            // sorted or duplicate
//            if (previousLine != null) {
//              val lineb = line.getBytes("UTF-8")
//              val previousLineb = previousLine.getBytes("UTF-8")
//              uncompressedSize += lineb.size + 1 //estimated bytesize count !!! this does not respect windows linefeeds properly (or potentially other control characters) that is why it is overwritten by stream information if possbile
//              val cmp = compareBytewise(lineb, previousLineb)
//              if (cmp == 0) {
//                dupes += 1
//              } else if (cmp < 0) {
//                def cut(s: String): String = {
//                  if (s.length > 41) s.substring(0, 40) else s
//                }
//
//                sort = false
//              }
//            }
//            previousLine = line
//
//          }
//
//          //now try to determine the accurate uncompressed byte size by reading it from underlying stream an override estimated one if possible
//          uncompressedSize = in match {
//            case c: CompressorInputStream => c.getBytesRead
//            case a: ArchiveInputStream => a.getBytesRead
//            case i: BufferedInputStream => databusModInput.file.size
//            case _ => uncompressedSize //log.warn(s"Bytesize only approximated for file: ${this.file.getAbsolutePath}")
//          }
//
//          nonEmptyLines = nonEmpty
//          duplicates = dupes
//          sorted = sort
//          uncompressedByteSize = uncompressedSize
//
//
//        } catch {
//          case e: Exception =>
//            //log.warn(s"Read Error for file: ${this.file.getAbsolutePath} not calculate line-based file metadata statistics (e.g. #empty lines) and also uncompressed byte-size for File")
//            nonEmptyLines = -1
//            duplicates = -1
//            sorted = false
//            uncompressedByteSize = -1
//        }
//      }
//
//      writeResultsToFiles(databusModInput, nonEmptyLines, duplicates, sorted, uncompressedByteSize)
//
//    } catch {
//      case e: Exception =>
//        log.error(s"failed to process ${databusModInput.id}")
//        print(databusModInput.modErrorFile.write(
//          Calendar.getInstance().getTime.toString + "\n" +
//            e.getStackTrace.mkString("\n")
//        ).pathAsString)
//    }
//
//  }
//
//  /**
//    * Opens the file with compression, etc.
//    * NOTE: if file is an archive, we assume it is only one file in it and this will be on the stream
//    *
//    * @return
//    */
//  def getInputStream(file: File): InputStream = {
//
//
//    lazy val archiveVariant: Option[String] = detectArchive(file)
//    lazy val compressionVariant: Option[String] = detectCompression(file)
//
//    val bis = new BufferedInputStream(file.newFileInputStream)
//
//    (compressionVariant, archiveVariant) match {
//
//      case (Some(comp), None) => {
//        new CompressorStreamFactory().createCompressorInputStream(bis)
//      }
//
//      case (None, Some(arch)) => {
//        new ArchiveStreamFactory().createArchiveInputStream(bis)
//      }
//
//      case (Some(comp), Some(arch)) => {
//        new ArchiveStreamFactory().createArchiveInputStream(new CompressorStreamFactory().createCompressorInputStream(bis))
//        //sys.error(s"file seems to be both compressed and an archive: $comp, $arch")
//
//      }
//
//      case (None, None) => bis
//
//    }
//  }
//
//  /**
//    * does a bytewise comparison in scala
//    *
//    * @param ab
//    * @param bb
//    * @return a negative value if a is in byte order before b, zero if a and b bytestreams match and, and a positive value else
//    */
//  def compareBytewise(ab: Array[Byte], bb: Array[Byte]): Int = {
//
//    val mLength = scala.math.min(ab.length, bb.length)
//
//    for (i <- 0 until mLength) {
//      if (ab(i) == bb(i)) {}
//      else
//        return toUnsignedByte(ab(i)).compareTo(toUnsignedByte(bb(i)))
//    }
//    return ab.length - bb.length
//  }
//
//  /**
//    * detect compression of a file
//    *
//    * @param datafile file to check compression from
//    * @return Option[Compression]
//    */
//  def detectCompression(datafile: File): Option[String] = {
//    try {
//      Some(CompressorStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
//    } catch {
//      case ce: CompressorException => None
//    }
//  }
//
//  /**
//    * detect archive of a file
//    *
//    * @param datafile file to check archive from
//    * @return Option[Archive]
//    */
//  def detectArchive(datafile: File): Option[String] = {
//    try {
//      Some(ArchiveStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
//    } catch {
//      case ce: ArchiveException => None
//    }
//  }
//
//  /**
//    * translates a java (signed!) byte into an unsigned byte (emulated via short)
//    *
//    * @param b signed byte to convert to unsigned byte value
//    * @return the unsigned byte value stored as short
//    */
//  def toUnsignedByte(b: Byte): Short = {
//    val aByte: Int = 0xff & b.asInstanceOf[Int]
//    aByte.asInstanceOf[Short]
//  }
//
//
//  /**
//    * write result into jena model
//    *
//    * @param databusModInput      input data on which calculations were carried out
//    * @param nonEmptyLines        number of non empty lines in file
//    * @param duplicates           number of duplicates in file
//    * @param sorted               is file sorted?
//    * @param uncompressedByteSize uncompressed byte size of file
//    * @return modModelHelper
//    */
//  def writeResultsToFiles(databusModInput: DatabusModInput, nonEmptyLines: Long, duplicates: Long, sorted: Boolean, uncompressedByteSize: Long): Unit = {
//
//    val externalResultFile = databusModInput.modResourceFile("filemetrics.ttl")
//
//    val modelHelper = new org.dbpedia.databus_mods.lib.util.DatabusModOutputHelper(
//      databusModInput,
//      config.volumes.localRepo, modName,
//      Some(externalResultFile)
//    )
//    val resultURI = modelHelper.getResultURI()
//
//    //create, feed, and write out external result model
//    val externalResultModel = ModelFactory.createDefaultModel()
//    externalResultModel.setNsPrefix("dataIdNsCore", "http://dataid.dbpedia.org/ns/core#")
//
//    // enable all SimpleNodeWrapper implicit conversions
//    import org.dbpedia.databus_mods.lib.util.SimpleNodeWrapper.implicits._
//    modelHelper.addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/core#sorted", sorted, externalResultModel)
//    modelHelper.addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/core#duplicates", duplicates, externalResultModel)
//    modelHelper.addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/core#uncompressedByteSize", uncompressedByteSize, externalResultModel)
//    modelHelper.addStmtToModel(resultURI, "http://dataid.dbpedia.org/ns/core#nonEmptyLines", nonEmptyLines, externalResultModel)
//    modelHelper.writeModel(externalResultModel, externalResultFile)
//
//    //write out meta data
//    modelHelper.writeMetaDataModels()
//  }
//
//}