# databus-online-stats
Prototype for Databus Describe to check whether all download links are working

## Motivation
Data providers on the bus publish their files with a core set of metadata descriptions like where to download them, filesize, debug info in short the DataId. 
Of course there ais much more metadata that users would like to have like:
Is the syntax correct, which vocabulary does the file use, etc. 

## Databus Describe
We allow third-parties to add further descriptions in the following manner:

* Add the webservice URL to the Databus, e.g. https://myservice.org/void-generator
* Download relevant data (e.g. daily) via https://databus.dbpedia.org/repo/sparql
* put interesting descriptions under:
  * http://myservice.org/$servicename/repo/$account/$group/$artifact/$version/$sha256sum.$fileending
* The Databus website, will link and display the results, when the processing is done
  * we prefer `.jsonld` and `.svg` files as they display well

## Requirements
* After processing web services are at least to return a JSON-LD summary. If additional descriptive data is available the JSON-LD can link to it
* the JSON-LD needs to be connected to the existing metadata in some way, ideally a databus identifier.

## Implementation of Online Stats

### Download updates (download.sh)
Once a day, we query the databus for this info:

```
QUERY="PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>

SELECT ?file ?sha256sum ?downloadURL   WHERE {
  ?s dcat:downloadURL ?downloadURL . 
  ?s dataid:sha256sum ?sha256sum .
  ?s dataid:file ?file .
} "

curl -d "format=text%2Ftab-separated-values" \
--data-urlencode "query=$QUERY" \
"https://databus.dbpedia.org/repo/sparql" > updates.tsv
```

### Generate online stats

#### Run

* Arg1: localpath to `repo`
* Arg2: onlinepath to `repo`
* Expects to find the updates.tsv from above in same folder:
```
mvn scala:run -DmainClass="check_if_online" -DaddArgs="/var/www/html/online/repo|http://88.99.242.78//online/repo"
```

#### Online Stats
The script is intended as a cronjob and checks whether all downloadURLs are reachable via HEAD requests and saves (append) the stats in $sha256sum.tsv files:
```
timestamp	success		downloadurl
```
From the tsv it calculates a JSON-LD summary which rates the online availability accoding to the historic data samples it collected as a percentage.

##### JSON-LD Example
Notes:
* http://dataid.dbpedia.org/ns/describe# is a free vocab, just invent properties
* we include links to the data (stats) and the svg
* subject uses the stable databus file identifier
 

```

{"@context": {
  	"desc": "http://dataid.dbpedia.org/ns/describe#",
	"onlinerate": { "@id": "desc:onlinerate","@type": "xsd:float"},
	"svg" : {"@id":"desc:svg","@type":"@id"},
	"stats" : {"@id":"desc:stats","@type":"@id"}
  },
 "@id": "https://databus.dbpedia.org/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/geo-coordinates-mappingbased_lang=ru.ttl.bz2",
 "onlinerate": "1.0",
 "svg": "http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.svg",
 "stats": "http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.tsv"

}

```
##### SVG
We used an existing template and replace color and text

