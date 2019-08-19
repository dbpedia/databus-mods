<?php
// config
$BASEDIR="./repo/";
error_reporting(E_ALL);
ini_set('display_errors', 1);

// parsing request parameters
$method = strtolower($_SERVER ["REQUEST_METHOD"]);
//TODO throw a bad request, if not set correctly, i.e. can only handle files
$file = $_REQUEST["file"] ;
$sha256sum = $_REQUEST["sha256sum"] ;
$downloadURL = $_REQUEST["downloadURL"] ;
// maps params to local repo
$tmp = str_replace("https://databus.dbpedia.org/","", $file) ;
$pos = strrpos($tmp, "/");
$path = substr($tmp, 0, $pos+1);
$filename = substr($path, $pos+1);
// simple ini for internal processing
$prefix = $path.$sha256sum;
$metadatafile = $prefix.".ini";
$svgfile = $prefix.".svg";
$summaryfile = $prefix.".jsonld";
$statfile = $prefix.".tsv";

//$fileprefix = $folder.substr($path, $pos+1);
// summary result files MUST be JSONLD and MAY link to further detailed data 
//$summaryfile = $fileprefix.".jsonld";
//$statfile = $fileprefix.".tsv";


// create a new description resource
if ($method === "put"){
	if(is_file($BASEDIR.$metadatafile)){
		header("HTTP/1.1 200 OK");
		//implement update, but not required here, since we work on the sha256sum
		echo "\nresource already exists\n";
	}else { 
		// validate values at databus
		// otherwise people might misuse the service for other things
		if(validate ($file, $downloadURL, $sha256sum) === "true"){
			// create the folder if not exist
			if(!is_dir($BASEDIR.$path)){
			  mkdir($BASEDIR.$path,0700,$recursive=true);
			}
			// write the metadata, we use .ini files here
			$contents = "[metadata]\n
				databusfile=$file\n
				downloadURL=$downloadURL\n
				sha256sum=$sha256sum\n
				prefix=$prefix\n
				summaryfile=$summaryfile\n
				statfile=$statfile\n
				svgfile=$svgfile";
			file_put_contents ($BASEDIR.$metadatafile , $contents);
			header("HTTP/1.1 201 Created");
		} else {
			header ("HTTP/1.1 400 Bad Request");
			echo "\nvalidation of request parameters at https://databus.dbpedia.org/repo/sparql failed, ASK returned false.\n";
		}
	}
	
// retrieve summary	
}else if ($method === "get"){
	if(!is_file($BASEDIR.$metadatafile)){
		header("HTTP/1.1 404 Not Found");
		echo "\nresource not yet created, use PUT first\n";
	}else if( !is_file ($BASEDIR.$summaryfile) ){
		header("HTTP/1.1 202 Accepted");
		echo "\nprocessing\n";
	}else {
		header("HTTP/1.1 200 OK");
		header("Content-Type: application/ld+json");
		$fp = fopen($BASEDIR.$summaryfile, 'r');
		header("Content-Length: " . filesize($BASEDIR.$summaryfile));
		fpassthru($fp);
	}
}

function summary(){}

function validate ($file, $downloadURL, $sha256sum) {
	
	$URL = "https://databus.dbpedia.org/repo/sparql";	
	$SPARQL = "
PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>

ASK WHERE {
  ?s dcat:downloadURL <$downloadURL> .
  ?s dataid:sha256sum \"$sha256sum\"^^xsd:string .
  ?s dataid:file <$file> .
  }" ;

	return file_get_contents($URL."?query=".urlencode($SPARQL));	
}

//print_r($_SERVER);
