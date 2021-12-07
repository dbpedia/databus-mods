# Databus Mods
Description and Demo for Databus Mods (incl/ Ontology and Process)
Contains a working mod to check whether all download links are working.
Please post ideas and endeavours to mod in the [DBpedia Forum](https://forum.dbpedia.org) 

## Structure

The project contains the following modules:
* `databus-mods-lib` core implementation, shared libraries, and utilities
* `databus-mods-server` reusable server implementation
* `databus-mods` basic Databus Mod Worker implementations and examples

## Build Your Mod

See [databus-mods/README.md](https://github.com/dbpedia/databus-mods/blob/master/databus-mods/README.md).

## Motivation
Data providers on the bus publish their files on their own servers with a core set of metadata descriptions like where to download them, filesize, license. 
These are captured in the `dataid.ttl` and hosted in the [Databus SPARQL API](https://dev.dbpedia.org/Download_Data).

* Mods are activities analysing the files or the DataId metadata on the Databus and provide usefull Stats, Enrichment and Ratings or visuals such as this SVG:  [![Build Status](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.svg)](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.html) 
* Mods allow any user to customize and extend the DBpedia Databus website with their own code and add a consistent layer of annotation over all files. 
* Mods can spot errors in data, provide links, fixes and patches or simply count rdf:type statements or words.

### Use cases and incentives
* If you would like to have consistent [VOID description](https://www.w3.org/TR/void/) for the whole bus, you can write a mod generating VOID for all datasets. 
* Mods are great to discover new datasets and data or generate stats for datasets. 
* Mods can be used to build platforms with your tools. You could create a Mod, where users can contribute mappings, links or patches, e.g. via [PatchR](https://hpi.de/fileadmin/user_upload/fachgebiete/meinel/Semantic-Technologies/slides/USEWOD2012.pdf) 


## Databus Mod Description
We allow third-parties to add further descriptions in the following manner:


### Getting updates
Mods need to query and download relevant metadata (e.g. daily).

Some examples:

```
# Ex1: query all file records
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
```
#Ex2: add a only recent record filter
  ?s dct:modified  ?mod .  
  # last five days
  Filter (?mod >= "2019-05-11T00:55:48Z"^^xsd:dateTime) 
```

### Full control over which datasets the mods run
Because each mod uses a SPARQL SELECT query, it can select exactly the datasets it will process.
Publishing your datasets on the databus and then writing mods for your data only, is possible. 




### Result of mods
![Prov-O relation to Mod](https://github.com/dbpedia/databus-mods/raw/master/doc/provo_databus-modrelation.png)


Here is an example description of the online-checker taken from:
http://88.99.242.78/online/repo/dbpedia/mappings/mappingbased-literals/2018.12.01/28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.ttl#this

```
@prefix mymod: <http://88.99.242.78/online/repo/dbpedia/mappings/mappingbased-literals/2018.12.01/> .
@prefix mymodvocab: <http://88.99.242.78/online/repo/modvocab.ttl#> .
@prefix mod: <http://dataid.dbpedia.org/ns/mod.ttl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix databus: <https://databus.dbpedia.org/> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.svg
    mod:svgDerivedFrom <https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=ro.ttl.bz2> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.html
    mod:htmlDerivedFrom <https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=ro.ttl.bz2> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.ttl#this
    mymodvocab:onlinerate "1.0"^^xsd:float ;
    a mymodvocab:OnlineTestMod ;
    prov:endedAtTime "2019-08-20T21:35:11.931+02:00[Europe/Berlin]"^^xsd:dateTime ;
    prov:generated mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.svg, mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.html .
```
and an example of the modvocab:
http://88.99.242.78/online/repo/modvocab.ttl

# Deprecated

## Databus SPARQL API
We will load the above descriptions of your mod into https://databus.dbpedia.org/repo/sparql .
Therefore we require:
1. the link to the modvocab.ttl 
2. the link to an aggregation, which we should load
See the example here: http://88.99.242.78/online/repo/ 

### Fair Use Limits
As of Aug, 2019 Databus has 20k files. Each activity produces at least 7 triples. Each additional report 2 triples. Which is 140k triples per mod minimum. We therefore expect mods to only generate maximum of two additional result entities next to the svg and html. Further reports can be linked from the HTML reports. This is only relevant, if you would like to mod all files or datasets. Since you write the SPARQL query, the limit increases, if the mod targets only 100 files or 5.     
Note: in the future, we are planning to move all the Mod data in a separate endpoint, then there can be more reports and the limit is raised. 

### Example queries:

```
# query all files and their mod results for dbpedia/mappings/mappingbased-literals/2018.12.01


PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX prov: <http://www.w3.org/ns/prov#>
PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dataiddebug: <http://dataid.dbpedia.org/ns/debug.ttl#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>

# group_concat see: https://stackoverflow.com/questions/18212697/aggregating-results-from-sparql-query
SELECT ?file (group_concat(?result;separator=",") as ?results)  WHERE {
  ?dataset dataid:version <https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01> .
  ?singlefile dataid:isDistributionOf ?dataset .
  ?singlefile dataid:file ?file .
  
  #?property rdfs:subPropertyOf prov:wasDerivedFrom . 
  #?result ?property ?file .
  ?resultsvg <http://dataid.dbpedia.org/ns/mod.ttl#svgDerivedFrom> ?file .
  # UPDATE statistics -> htmlDerivedFrom
  ?resultstat <http://dataid.dbpedia.org/ns/mod.ttl#htmlDerivedFrom> ?file .
  # from the same activity on the same file
  ?activity prov:generated ?resultsvg .
  ?activity prov:generated ?resultstat .
  # UPDATE added line below
  ?activity prov:used ?file .
  
  # transform to image link
  # <img src="smiley.gif" alt="Smiley face" height="42" width="42"> 
  BIND (concat("<a href=\"",?resultstat, "\"> <img src=\"",?resultsvg,"\"></a>" ) AS ?result )
  # optionally getting label of the mod
  # ?activity prov:generated ?result .
  # ?activity a ?mod .
     
} 
Group by ?file 

```


## URLs of the Mod (just an example)
For activities (one analysis process): 

* http://myservice.org/$servicename/repo/$account/$group/$artifact/$version/$sha256sum.ttl

For entities (results):

* http://myservice.org/$servicename/repo/$account/$group/$artifact/$version/$sha256sum.$fileending

For easier handling an aggregated file:

* http://myservice.org/$servicename/repo/aggregate.nt

For the modvocab and description:

* http://myservice.org/$servicename/repo/modvocab.ttl



## Implementaion of Online Check Mod 

Code is in this repo under [`online-check-mod`](https://github.com/dbpedia/databus-mods/tree/master/online-check-mod)

The service dumps all results in the `repo` folder: http://88.99.242.78/online/repo/


### Compile and Run


* Arg1: localpath to `repo`, in our deployment: `/var/www/html/online/repo`
* Arg2: onlinepath to `repo`, in our deployment: `http://88.99.242.78//online/repo` 
* Expects to find the updates.tsv from above in same folder:

```
cd online-check-mod
mvn clean compile 
mvn scala:run -DmainClass="check_if_online" -DaddArgs="/var/www/html/online/repo|http://88.99.242.78//online/repo"
```
### Cronjob, daily at 3 am

```
# m h  dom mon dow   command
0 3 * * * cd /root/databus-mods/online-check-mod && ./download-updates.sh && mvn scala:run -DmainClass="check_if_online" -DaddArgs="/var/www/html/online/repo|http://88.99.242.78/online/repo"
```

### Results

Read more about the mod in the modvocab.ttl: http://88.99.242.78/online/repo/modvocab.ttl

#### HTML Online Stats
The script is intended as a cronjob and checks whether all downloadURLs are reachable via HEAD requests and saves (append) the stats in $sha256sum.html files:
http://88.99.242.78/online/repo/dbpedia/mappings/mappingbased-literals/2018.12.01/28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.html
```
timestamp	success		downloadurl
```

#### SVG
We used an existing template and replace color and text:

 [![Build Status](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.svg)](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.html)

