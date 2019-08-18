#!/usr/bin/env amm


import scala.io.Source
import java.io.File
import $ivy.`org.ini4j:ini4j:0.5.4`
import $ivy.`org.apache.httpcomponents:httpclient:4.5.9`
import org.ini4j.Ini
import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClients

@main
def main(repo: String) = {
	
	val repoDir = new File (repo)
	val inis = getRecursiveListOfFiles(repoDir).toList.filter(_.isFile).filter(_.toString.endsWith(".ini")).toList

	inis.foreach(f=>{
		val ini = new Ini(f);
		val summaryfile = repo+ini.get("metadata", "summaryfile")
		val downloadURL = ini.get("metadata", "downloadURL")
		val fileprefix = repo+ini.get("metadata", "fileprefix")	
		val httpclient = HttpClients.createDefault();
		val httpHead = new HttpHead(downloadURL);
		var success = false
		try{
			val code = httpclient.execute(httpHead).getStatusLine.getStatusCode;
			if(code==200){
				success = true
				}
		
		}catch{ case _: Throwable => success=false }
		
		val timestamp: Long = System.currentTimeMillis / 1000
		val stat=timestamp+"\t"+success+"\t"+downloadURL
		println (stat)
		println (summaryfile)
		println (success)
		// check status
		// write to table
		})
	
	
	
    }

def getRecursiveListOfFiles(dir: File): Array[File] = {
    val these = dir.listFiles
    these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
}
