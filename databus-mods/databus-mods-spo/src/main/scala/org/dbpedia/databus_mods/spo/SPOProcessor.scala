package org.dbpedia.databus_mods.spo

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

import org.apache.jena.graph.Triple
import org.apache.jena.riot.lang.PipedRDFIterator
import org.dbpedia.databus_mods.lib.util.{IORdfUtil, UriUtil}
import org.dbpedia.databus_mods.lib.worker.base.{DataIDExtension, Process}
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import scala.collection.mutable

/**
 * calculate the number of occurrences of each subject, predicate, and object of rdf file
 */
@Component
class SPOProcessor extends Process {

  private val log = LoggerFactory.getLogger(classOf[SPOProcessor])

  override def run(ext: DataIDExtension): Unit = {
    try {
      val (s, p, o) = calculateSPO(IORdfUtil.toPipedRDF(UriUtil.openStream(ext.source)))
      val os = ext.createModResult("spo.tsv")
      val osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)
      write(osw, s, "subject")
      write(osw, p, "predicate")
      write(osw, o, "object")
      os.flush()
      os.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
   * calculate number of occurrences of each subject, predicate, and object of rdf iterator
   *
   * @param iter rdf iterator
   * @return subjectMap, predicateMap, objectMap
   */
  def calculateSPO(iter: PipedRDFIterator[Triple]): (mutable.HashMap[String, Int], mutable.HashMap[String, Int], mutable.HashMap[String, Int]) = {
    val subjectMap: mutable.HashMap[String, Int] = mutable.HashMap.empty
    val predicateMap: mutable.HashMap[String, Int] = mutable.HashMap.empty
    val objectMap: mutable.HashMap[String, Int] = mutable.HashMap.empty

    while (iter.hasNext) {
      val triple = iter.next()
      val subj = {
        if (triple.getSubject.isURI) triple.getSubject.getURI
        else ""
      }
      val obj = {
        if (triple.getObject.isURI) triple.getObject.getURI
        else ""
      }
      val pre = triple.getPredicate.getURI

      if (subj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(subjectMap, subj)
      increaseCountIfExistsOrAddToMapIfNotExists(predicateMap, pre)
      if (obj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(objectMap, obj)
    }

    (subjectMap, predicateMap, objectMap)
  }

  /**
   * increase number of occurrences, or add element if not exists yet
   *
   * @param anyMap map
   * @param elem   element to check
   */
  def increaseCountIfExistsOrAddToMapIfNotExists(anyMap: mutable.HashMap[String, Int], elem: String): Unit = {
    anyMap.get(elem) match {
      case Some(count) => anyMap.update(elem, count + 1)
      case None => anyMap.put(elem, 1)
    }
  }

  /**
   *
   * @param osw                 sink
   * @param elementMap          term count map
   * @param elementPositionName term triple position
   */
  def write(osw: OutputStreamWriter, elementMap: mutable.HashMap[String, Int], elementPositionName: String): Unit = {
    while (elementMap.nonEmpty) {
      if (elementMap.head._1.contains(";")) osw.append(s""""${elementMap.head._1}";$elementPositionName;${elementMap.head._2}\n""")
      else osw.append(s"${elementMap.head._1};$elementPositionName;${elementMap.head._2}\n")
      elementMap.remove(elementMap.head._1)
    }
  }

}