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
$path = str_replace("https://databus.dbpedia.org/","", $file) ;
$pos = strrpos($path, "/");
$folder = substr($path, 0, $pos+1);
// simple ini for internal processing
$metadatafile = $folder.$sha256sum.".ini";
$fileprefix = $folder.substr($path, $pos+1);
// summary result files MUST be JSONLD and MAY link to further detailed data 
$summaryfile = $fileprefix.".jsonld";


// create a new description resource
if ($method === "put"){
	if(is_file($BASEDIR.$metadatafile)){
		header("HTTP/1.1 200 OK");
		echo "\nresource already exists\n";
	}else { 
		// validate values at databus
		// otherwise people might misuse the service for other things
		if(validate ($file, $downloadURL, $sha256sum) === "true"){
			// create the folder if not exist
			if(!is_dir($BASEDIR.$folder)){
			  mkdir($BASEDIR.$folder,0700,$recursive=true);
			}
			// write the metadata, we use .ini files here
			$contents = "[metadata]\nfileprefix=$fileprefix\nsummaryfile=$summaryfile\ndownloadURL=$downloadURL\n";
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
