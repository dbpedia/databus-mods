package org.dbpedia.databus_mods.uri_analyzor

import java.util.Calendar

import org.apache.jena.graph.Triple
import org.apache.jena.iri.IRIFactory
import org.apache.jena.riot.lang.PipedRDFIterator
import org.dbpedia.databus_mods.lib.{AbstractDatabusModExecutor, DatabusModInput}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.mutable
import org.dbpedia.databus_mods.lib.util.RdfFileHelpers
import scala.collection.JavaConversions._

@Service
class DatabusModExecutor @Autowired()(config: Config) extends AbstractDatabusModExecutor {

  private val log = LoggerFactory.getLogger(classOf[DatabusModExecutor])
  private implicit val basePath: String = config.volumes.localRepo

  def process(databusModInput: DatabusModInput): Unit = {
    try {
      log.info(s"process ${databusModInput.id}")
      val metadataFile = databusModInput.modMetadataFile
      metadataFile.parent.createDirectories()
      // TODO your code

      val iter =  RdfFileHelpers.readAsTriplesIterator(databusModInput.file)
      print(calculateSPO(iter))

      //throw new Exception("evil exception")
    } catch {
      case e: Exception =>
        log.error(s"failed to process ${databusModInput.id}")
        print(databusModInput.modErrorFile.write(
          Calendar.getInstance().getTime.toString + "\n" +
            e.getStackTrace.mkString("\n")
        ).pathAsString)
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

//      if (subj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(subjectMap, subj)
//      increaseCountIfExistsOrAddToMapIfNotExists(predicateMap, pre)
//      if (obj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(objectMap, obj)
      if (subj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(subjectMap, getIriPrefix(subj))
      increaseCountIfExistsOrAddToMapIfNotExists(predicateMap, getIriPrefix(pre))
      if (obj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(objectMap, getIriPrefix(obj))

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

  def getIriPrefix(uri : String ) = {
    val ifactory = IRIFactory.iriImplementation()
    val iri = ifactory.create(uri)
    ifactory.construct(iri.getScheme,iri.getRawAuthority,"","","")
    iri.getScheme+iri.getRawAuthority
  }



}