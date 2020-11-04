package org.dbpedia.databus_mods.spo

import java.io.{BufferedWriter, FileWriter}
import java.util.Calendar

import better.files.File
import org.apache.jena.graph.Triple
import org.apache.jena.riot.lang.PipedRDFIterator
import org.dbpedia.databus_mods.lib.util.RdfFileHelpers
import org.dbpedia.databus_mods.lib.{AbcDatabusModConfig, DatabusModInput}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
* calculate the number of occurrences of each subject, predicate, and object of rdf file
*/
class SPOProcessor(config: AbcDatabusModConfig) {

  private val log = LoggerFactory.getLogger(classOf[SPOProcessor])
  private implicit val basePath: String = config.volumes.localRepo
  private val modName = config.name

  /**
    * calculate number of occurrences of each subject, predicate, and object of rdf iterator
    *
    * @param databusModInput mod input data
    */
  def process(databusModInput: DatabusModInput): Unit = {
    try {
      log.info(s"process ${databusModInput.id}")

      val iter = RdfFileHelpers.readAsTriplesIterator(databusModInput.file)

      if (iter.hasNext) {
        val spo = calculateSPO(iter)
        writeResultsToFiles(databusModInput, spo._1, spo._2, spo._3)
      } else {
        throw new Exception("iterator empty")
      }
    }
    catch {
      case e: Exception =>
        log.error(s"failed to process ${databusModInput.id}")
        e.printStackTrace()
        databusModInput.modErrorFile.parent.createDirectories()
        databusModInput.modErrorFile.write(
          Calendar.getInstance().getTime.toString + "\n" +
            e.getStackTrace.mkString("\n")
        )
    }
  }

  /**
    * calculate number of occurrences of each subject, predicate, and object of rdf iterator
    *
    * @param iter rdf iterator
    * @return subjectMap, predicateMap, objectMap
    */
  def calculateSPO(iter:PipedRDFIterator[Triple]):(mutable.HashMap[String,Int],mutable.HashMap[String,Int],mutable.HashMap[String,Int])={
    val subjectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
    val predicateMap: mutable.HashMap[String,Int] = mutable.HashMap.empty
    val objectMap: mutable.HashMap[String,Int] = mutable.HashMap.empty

    while (iter.hasNext) {
      val triple = iter.next()
      val subj = {
        if(triple.getSubject.isURI) triple.getSubject.getURI
        else ""
      }
      val obj = {
        if(triple.getObject.isURI) triple.getObject.getURI
        else ""
      }
      val pre = triple.getPredicate.getURI

      if(subj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(subjectMap,subj)
      increaseCountIfExistsOrAddToMapIfNotExists(predicateMap,pre)
      if(obj.nonEmpty) increaseCountIfExistsOrAddToMapIfNotExists(objectMap,obj)
    }

    (subjectMap,predicateMap,objectMap)
  }

  /**
    * increase number of occurrences, or add element if not exists yet
    *
    * @param anyMap map
    * @param elem   element to check
    */
  def increaseCountIfExistsOrAddToMapIfNotExists(anyMap:mutable.HashMap[String,Int], elem:String):Unit ={
    anyMap.get(elem) match {
      case Some(count) => anyMap.update(elem, count+1)
      case None => anyMap.put(elem,1)
    }
  }

  /**
    * write results to csv file
    *
    * @param resultFile file to write to
    * @param subjectMap
    * @param predicateMap
    * @param objectMap
    */
  def writeExternalResult(resultFile:File, subjectMap:mutable.HashMap[String,Int], predicateMap:mutable.HashMap[String,Int], objectMap:mutable.HashMap[String,Int]):Unit={
    resultFile.parent.createDirectories()
    val bw = new BufferedWriter(new FileWriter(resultFile.toJava,false))

    write(subjectMap, "subject")
    write(predicateMap, "predicate")
    write(objectMap, "object")

    bw.close()

    /**
     * write map to csv
     *
     * @param myMap map
     * @param spo   subject,predicate, or object
     */
    def write(myMap:mutable.HashMap[String,Int], spo:String):Unit={
      while(myMap.nonEmpty) {
        if (myMap.head._1.contains(";")) bw.append(s""""${myMap.head._1}";$spo;${myMap.head._2}\n""")
        else bw.append(s"${myMap.head._1};$spo;${myMap.head._2}\n")
        myMap.remove(myMap.head._1)
      }
    }
  }


  /**
   *
   * @param databusModInput input data on which calculations were carried out
   * @param subjectMap
   * @param predicateMap
   * @param objectMap
   */
  def writeResultsToFiles(databusModInput: DatabusModInput, subjectMap:mutable.HashMap[String,Int], predicateMap:mutable.HashMap[String,Int], objectMap:mutable.HashMap[String,Int]): Unit = {

    val externalResultFile = databusModInput.modMetadataFile(basePath).parent / "spo.csv"

    val modelHelper = new org.dbpedia.databus_mods.lib.util.DatabusModOutputHelper(
      databusModInput,
      config.volumes.localRepo, modName,
      Some(externalResultFile)
    )

    writeExternalResult(externalResultFile, subjectMap, predicateMap, objectMap)

    //write out meta data
    modelHelper.writeMetaDataModels()
  }

}