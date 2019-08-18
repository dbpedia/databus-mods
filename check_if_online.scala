#!/usr/bin/env amm


import scala.io.Source
import java.io.File
import $ivy.`org.ini4j:ini4j:0.5.4`
import org.ini4j.Ini

@main
def main(repo: String) = {
	
	val repoDir = new File (repo)
	val inis = getRecursiveListOfFiles(repoDir).toList.filter(_.isFile).filter(_.toString.endsWith(".ini")).toList

	inis.foreach(f=>{
		val ini = new Ini(f);
		val summaryfile = repo+ini.get("metadata", "summaryfile")
		val downloadurl = ini.get("metadata", "downloadURL")
		val fileprefix = repo+ini.get("metadata", "fileprefix")	
		println (summaryfile)
		// check status
		// write to table
		})
	
	
	
    }

def getRecursiveListOfFiles(dir: File): Array[File] = {
    val these = dir.listFiles
    these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
}
