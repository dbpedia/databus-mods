///*-
// * #%L
// * Indexing the Databus
// * %%
// * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * #L%
// */
//import java.io.{BufferedInputStream, BufferedWriter, FileInputStream, FileWriter}
//import java.util.concurrent.{ExecutorService, Executors}
//
//import better.files.File
//import org.apache.jena.graph.{NodeFactory, Triple}
//import org.apache.jena.riot.lang.{PipedRDFIterator, PipedRDFStream, PipedTriplesStream}
//import org.apache.jena.riot.{Lang, RDFDataMgr}
//import org.dbpedia.databus.client.filehandling.convert.compression.Compressor
//import org.scalatest.flatspec.AnyFlatSpec
//
//import scala.collection.mutable
//
//class SPOProcessorTests extends AnyFlatSpec{
//
//  "test" should "return all included subjects,objects and predicates" in {
//
////      val file = File("/home/eisenbahnplatte/git/DBpedia/databus-mods/indexer/src/test/resources/geo-coordinates-mappingbased_lang=de.ttl.bz2")
//    val file = File("/home/eisenbahnplatte/git/DBpedia/databus-mods/indexer/src/test/resources/ntriples.nt")
//    val bis = new BufferedInputStream(new FileInputStream(file.toJava))
//
//      val in = Compressor.decompress(bis)
//
//      val iter: PipedRDFIterator[Triple] = new PipedRDFIterator[Triple]()
//      val rdfStream: PipedRDFStream[Triple] = new PipedTriplesStream(iter)
//
//      val rdfType = NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
//      val subjectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
//      val predicateMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
//      val objectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
//
//      // PipedRDFStream and PipedRDFIterator need to be on different threads
//      val executor: ExecutorService = Executors.newSingleThreadExecutor()
//
//      // Create a runnable for our parser thread
//      val parser: Runnable = new Runnable() {
//        override def run() {
//          // Call the parsing process.
//          RDFDataMgr.parse(rdfStream, in, Lang.TTL)
//        }
//      }
//
//      // Start the parser on another thread
//      executor.submit(parser)
//
//
//      while (iter.hasNext) {
//        val triple = iter.next()
//        val subj = triple.getSubject.toString
//        val obj = triple.getObject.toString
//        val pre = triple.getPredicate.getURI
//
//        subjectMap.get(subj) match {
//          case Some(count) => subjectMap.update(subj, count+1)
//          case None => subjectMap.put(subj,1)
//        }
//        predicateMap.get(pre) match {
//          case Some(count) => predicateMap.update(pre, count+1)
//          case None => predicateMap.put(pre,1)
//        }
//        objectMap.get(obj) match {
//          case Some(count) => objectMap.update(obj, count+1)
//          case None => objectMap.put(obj,1)
//        }
//      }
//
//    val resultfile = File(s"./${file.nameWithoutExtension(true)}_spoResult.csv")
//    val bw = new BufferedWriter(new FileWriter(resultfile.toJava, true))
//    bw.append("subjects, countSubject, predicates, countPredicate, objects, countObject\n")
//
//    println("subjects")
//    subjectMap.foreach(println(_))
//    println("predicxtes")
//    predicateMap.foreach(println(_))
//    println("objhects")
//    objectMap.foreach(println(_))
//
//    while(subjectMap.nonEmpty || objectMap.nonEmpty || predicateMap.nonEmpty){
//      var str = StringBuilder.newBuilder
//      if (subjectMap.nonEmpty) {
//        str.append(s"${subjectMap.head._1},${subjectMap.head._2},")
//        subjectMap.remove(subjectMap.head._1)
//      }
//      else str.append(",,")
//
//      if(predicateMap.nonEmpty) {
//        str.append(s"${predicateMap.head._1},${predicateMap.head._2},")
//        predicateMap.remove(predicateMap.head._1)
//      }
//      else str.append(",,")
//
//      if(objectMap.nonEmpty) {
//        str.append(s"${objectMap.head._1},${objectMap.head._2}\n")
//        objectMap.remove(objectMap.head._1)
//      }
//      else str.append(",\n")
//
//      bw.append(str)
//    }
//
//    bw.close()
//  }
//
//  "hashmap" should "be correct" in {
//    val myMap:mutable.HashMap[String,Int] = mutable.HashMap.empty
//    myMap.put("asd",2)
//    myMap.put("bsf",3)
//    myMap.put("rakf",4)
//
//    myMap.foreach(println(_))
//
//    println("und jetzt?")
//    myMap.remove(myMap.head._1)
//    myMap.foreach(println(_))
//  }
//
//  "multiline objects" should "be recognized" in {
//
//    val multilineStr= """An OWL representation of part of the model for geometry and space from ISO 19107:2003 Geographic Information - Spatial Schema.
//
//This vocabulary is provisional, pending finalization of ISO 19150-2.
//
//The URI stem http://def.seegrid.csiro.au/isotc211/ is temporary. The vocabulary is expected to be ultimately published in the domain http://def.isotc211.org/. """
//
//    if (multilineStr.contains("\n")) println("yes")
//    else println("no")
//  }
//}
