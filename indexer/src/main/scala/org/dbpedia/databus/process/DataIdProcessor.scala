package org.dbpedia.databus.process

import java.io.{BufferedInputStream, InputStream}

import better.files.File
import org.apache.commons.compress.archivers.{ArchiveException, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.compressors.{CompressorException, CompressorInputStream, CompressorStreamFactory}
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.io.{Codec, Source}

class DataIdProcessor extends Processor {

  var nonEmptyLines : Long = 0L
  var duplicates : Long= 0L
  var sorted: Boolean = false
  var uncompressedByteSize : Long = 0L

  /**
   * calculate all void:propertyPartition together with its number of occurrences and the void:classPartition of a processed file
   *
   * @param file local file to process
   * @param item item of processed file
   * @param sink sink result
   */
  override def process(file: File, item: Item, sink: Sink): Unit = {
      calculateFileMetrics(file)
//    sink.consume(resultAsTurtle)

  }

  def calculateFileMetrics(file: File): Unit = {

    var nonEmpty = 0L
    var dupes = 0L
    var sort: Boolean = true
    var charSize = 0L
    var uncompressedSize = 0L

    var previousLine: String = null
    try {
      val in = getInputStream(file)

      val it: Iterator[String] = Source.fromInputStream(in)(Codec.UTF8).getLines()
      while (it.hasNext) {
        val line = it.next()
        charSize += line.length //now counts the number of chars (not including linefeeds)

        // non empty lines
        if (!line.trim.isEmpty) {
          nonEmpty += 1
        }

        // sorted or duplicate
        if (previousLine != null) {
          val lineb = line.getBytes("UTF-8")
          val previousLineb = previousLine.getBytes("UTF-8")
          uncompressedSize += lineb.size + 1 //estimated bytesize count !!! this does not respect windows linefeeds properly (or potentially other control characters) that is why it is overwritten by stream information if possbile
          val cmp = compareBytewise(lineb, previousLineb)
          if (cmp == 0) {
            dupes += 1
          } else if (cmp < 0) {
            def cut(s: String): String = {
              if (s.length > 41) s.substring(0, 40) else s
            }

            //log.debug("Sortorder non-ascii line " + nonEmpty + ": |" + cut(previousLine) + ">" + cut(line) + "|")
            sort = false
          }
        }
        previousLine = line

      }

      //now try to determine the accurate uncompressed byte size by reading it from underlying stream an override estimated one if possible
      uncompressedSize = in match {
        case c: CompressorInputStream => c.getBytesRead
        case a: ArchiveInputStream => a.getBytesRead
        case i: BufferedInputStream => file.size
        case _ => uncompressedSize//log.warn(s"Bytesize only approximated for file: ${this.file.getAbsolutePath}");
      }

      nonEmptyLines = nonEmpty
      duplicates = dupes
      sorted = sort
      uncompressedByteSize = uncompressedSize
    } catch {
      /*case mfe: MalformedInputException =>
        nonEmptyLines = nonEmpty
        duplicates = dupes
        sorted = sort
        uncompressedByteSize = uncompressedSize */
      case e: Exception =>
        //log.warn(s"Read Error for file: ${this.file.getAbsolutePath} not calculate line-based file metadata statistics (e.g. #empty lines) and also uncompressed byte-size for File")
        nonEmptyLines = -1
        duplicates = -1
        sorted = false
        uncompressedByteSize = -1
    }


    println(nonEmptyLines)
    println(duplicates)
    println(sorted)
    println(uncompressedByteSize)
  }

  /**
    * translates a java (signed!) byte into an unsigned byte (emulated via short)
    *
    * @param b signed byte to convert to unsigned byte value
    * @return the unsigned byte value stored as short
    */
  def toUnsignedByte(b: Byte):Short = {
    val aByte: Int = 0xff & b.asInstanceOf[Int]
    aByte.asInstanceOf[Short]
  }

  /**
    * does a bytewise comparison in scala
    *
    * @param ab
    * @param bb
    * @return a negative value if a is in byte order before b, zero if a and b bytestreams match and, and a positive value else
    */
  def compareBytewise(ab: Array[Byte], bb: Array[Byte]): Int = {

    val mLength = scala.math.min(ab.length, bb.length)

    for (i <- 0 to mLength - 1) {
      if (ab(i) == bb(i)) {}
      else
        return toUnsignedByte(ab(i)).compareTo(toUnsignedByte(bb(i)))
    }
    return ab.length - bb.length
  }

  //  /**
  //   * does a bytewise string comparison in scala similar to LC_ALL=C sort does in Unix
  //   *
  //   * @param a
  //   * @param b
  //   * @return a negative value if a is in byte order before b, zero if a and b bytestreams match and, and a positive value else
  //   */
  //  def compareStringsBytewise(a: String, b: String): Int = {
  //    val ab = a.getBytes("UTF-8")
  //    val bb = b.getBytes("UTF-8")
  //
  //    compareBytewise(ab, bb)
  //  }

  /**
   * Opens the file with compression, etc.
   * NOTE: if file is an archive, we assume it is only one file in it and this will be on the stream
   *
   * @return
   */
  def getInputStream(file: File): InputStream ={


    lazy val archiveVariant: Option[String] = detectArchive(file)
    lazy val compressionVariant: Option[String] = detectCompression(file)

    val bis = new BufferedInputStream(file.newFileInputStream)

    (compressionVariant, archiveVariant) match {

      case (Some(comp), None) => {
        new CompressorStreamFactory().createCompressorInputStream(bis)
      }

      case (None, Some(arch)) => {
        new ArchiveStreamFactory().createArchiveInputStream(bis)
      }

      case (Some(comp), Some(arch)) => {
        new ArchiveStreamFactory().createArchiveInputStream(new CompressorStreamFactory().createCompressorInputStream(bis))
        //sys.error(s"file seems to be both compressed and an archive: $comp, $arch")

      }

      case (None, None) => bis

    }
  }

  def detectCompression(datafile: File): Option[String] = {
    try {
      Some(CompressorStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
    } catch {
      case ce: CompressorException => None
    }
  }

  def detectArchive(datafile: File): Option[String] = {
    try {
      Some(ArchiveStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
    } catch {
      case ce: ArchiveException => None
    }
  }

}
