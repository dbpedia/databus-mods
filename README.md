# databus-mods
Prototype for Databus Mods (Ontology and Process)
Contains a working mod to check whether all download links are working

## Motivation
Data providers on the bus publish their files with a core set of metadata descriptions like where to download them, filesize in short the DataId.
Mods are Activities analysing the files or the metadata and provide usefull Stats, Enrichment and Ratings.
 
## Databus Mods
We allow third-parties to add further descriptions in the following manner.

### Getting updates
Mods can query and download relevant metadata (e.g. daily) via https://databus.dbpedia.org/repo/sparql 
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

### Result of mods
![Prov-O relation to Mod](https://github.com/dbpedia/databus-mods/raw/master/provo_databus-modrelation.png)


Here is an example description of the online-checker

```
@prefix mymod: <http://88.99.242.78/online/repo/dbpedia/mappings/mappingbased-literals/2018.12.01/> .
@prefix mymodvocab: <http://88.99.242.78/online/repo/modvocab.ttl#> .
@prefix mod: <http://dataid.dbpedia.org/ns/mod.ttl#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix databus: <https://databus.dbpedia.org/> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.svg
    mod:svgDerivedFrom <https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=ro.ttl.bz2> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.tsv
    mod:statisticsDerivedFrom <https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=ro.ttl.bz2> .

mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.ttl#this
    mymodvocab:onlinerate "1.0"^^xsd:float ;
    a mymodvocab:OnlineTestMod ;
    prov:endedAtTime "2019-08-20T21:35:11.931+02:00[Europe/Berlin]"^^xsd:dateTime ;
    prov:generated mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.svg, mymod:28bf5ba354072b99bca31e59294755a0dbfa392566044943b2e881a5a9370a73.tsv .
```

## Databus SPARQL API
We will load the above description of your mode into https://databus.dbpedia.org/repo/sparql .

### Requirements:
* We expect this to be 5-10 triples per record linking to much more data, visualisations and detailed reports.
* Please look at the [mod.ttl](mod.ttl) and use subproperties of `prov:wasDerivedFrom` and 

Example query:

```
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX prov: <http://www.w3.org/ns/prov#>
PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>

SELECT ?file ?property ?result WHERE {
  ?s dataid:file ?file . 
  ?property rdfs:subPropertyOf prov:wasDerivedFrom . 
  ?result ?property ?file .
  # optionally getting the mod
  # ?activity prov:generated ?result .
  # ?activity a ?mod .
    
} 

```


## URLs of the Mod (draft, not binding)
For activities (one analysis process): 

* http://myservice.org/$servicename/repo/$account/$group/$artifact/$version/$sha256sum.ttl

For entities (results):

* http://myservice.org/$servicename/repo/$account/$group/$artifact/$version/$sha256sum.$fileending

For easier handling an aggregated file:

* http://myservice.org/$servicename/repo/aggregate.nt

For the modvocab and description:

* http://myservice.org/$servicename/repo/modvocab.ttl



## Implementaion of Online Check Mod 

Code is in this repo 

The service dumps all results here:

* http://88.99.242.78/online/repo/


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

#### TSV Online Stats
The script is intended as a cronjob and checks whether all downloadURLs are reachable via HEAD requests and saves (append) the stats in $sha256sum.tsv files:
```
timestamp	success		downloadurl
```

#### SVG
We used an existing template and replace color and text:

 [![Build Status](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.svg)](http://88.99.242.78/online/repo/dbpedia/mappings/geo-coordinates-mappingbased/2018.12.01/978e5a0884ccbefbedb2c699d385247fd52d5968e013cd7f0dbec98124eb64b3.jsonld)

