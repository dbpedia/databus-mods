/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.mod.process

import java.io.{BufferedInputStream, InputStream}

import better.files.File
import org.apache.commons.compress.archivers.{ArchiveException, ArchiveInputStream, ArchiveStreamFactory}
import org.apache.commons.compress.compressors.{CompressorException, CompressorInputStream, CompressorStreamFactory}
import org.dbpedia.databus.indexer.Item
import org.dbpedia.databus.sink.Sink

import scala.io.{Codec, Source}

/**
  * The DataIDProcessor has the task to calculate the number of non-empty lines of a file, as well as the uncompressed byte size, the number of duplicates(triples).
  * It also checks if the file is sorted.
  */
@SerialVersionUID(1L)
class DataIdProcessor extends Processor {

  var nonEmptyLines: Long = 0L
  var duplicates: Long = 0L
  var sorted: Boolean = false
  var uncompressedByteSize: Long = 0L

  /**
    * calculate some metrics of a file:
    * - non-empty lines
    * - duplicates
    * - sorted?
    * - uncompressed byte size
    *
    * @param file file to process
    * @param item item of processed file
    * @param sink sink to give result to
    */
  override def process(file: File, item: Item, sink: Sink): Unit = {
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
        case _ => uncompressedSize //log.warn(s"Bytesize only approximated for file: ${this.file.getAbsolutePath}")
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


    sink.consume(
      s"""
         |nonEmptyLines: $nonEmptyLines
         |duplicates: $duplicates
         |sorted: $sorted
         |uncompressedByteSize: $uncompressedByteSize
      """.stripMargin)

  }

  /**
    * Opens the file with compression, etc.
    * NOTE: if file is an archive, we assume it is only one file in it and this will be on the stream
    *
    * @return
    */
  def getInputStream(file: File): InputStream = {


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

  /**
    * detect compression of a file
    *
    * @param datafile file to check compression from
    * @return Option[Compression]
    */
  def detectCompression(datafile: File): Option[String] = {
    try {
      Some(CompressorStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
    } catch {
      case ce: CompressorException => None
    }
  }

  /**
    * detect archive of a file
    *
    * @param datafile file to check archive from
    * @return Option[Archive]
    */
  def detectArchive(datafile: File): Option[String] = {
    try {
      Some(ArchiveStreamFactory.detect(new BufferedInputStream(datafile.newFileInputStream)))
    } catch {
      case ce: ArchiveException => None
    }
  }

  /**
    * translates a java (signed!) byte into an unsigned byte (emulated via short)
    *
    * @param b signed byte to convert to unsigned byte value
    * @return the unsigned byte value stored as short
    */
  def toUnsignedByte(b: Byte): Short = {
    val aByte: Int = 0xff & b.asInstanceOf[Int]
    aByte.asInstanceOf[Short]
  }

}
