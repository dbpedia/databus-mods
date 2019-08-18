# databus-online-stats
Implementation of a Databus Describe web service to check whether all download links are working

## Design
Data providers on the bus publish their files with a core set of metadata descriptions like where to download them, filesize, debug info in short the DataId.
We allow third-parties to add further descriptions in the following manner:
* Add the webservice URL to the Databus, e.g. https://myservice.org/void-generator
* Databus will ping all web services with a PUT request adding information to the request parameters
* After the ping the Describe web services process the files at their own speed
* The Databus website, will include and display the results, when the processing is done

## Requirements
* Strictly REST, PUT creates the resources, GET retrieves them
* During processing web services are to return 202 Accepted
* After processing web services are to return a JSON-LD summary. If additional descriptive data is available the JSON-LD can link to it

Furthermore, web services should validate correctness of parameters to prevent misuse and cache results.


### Example PUT
```
# create new resource
curl -X PUT "http://localhost/online/index.php?\
file=https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2&\
sha256sum=cf52dda5ef16f823702aba3f41db14e4f2d1f758e88070158eed331eeb609ec5&\
downloadURL=https://downloads.dbpedia.org/repo/lts/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2"
 
```
### Example GET (same URL)
```
# retrieve summary
curl -X GET "http://localhost/online/index.php?\
file=https://databus.dbpedia.org/dbpedia/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2&\
sha256sum=cf52dda5ef16f823702aba3f41db14e4f2d1f758e88070158eed331eeb609ec5&\
downloadURL=https://downloads.dbpedia.org/repo/lts/mappings/mappingbased-literals/2018.12.01/mappingbased-literals_lang=hi.ttl.bz2"
 
```

## Online Stats
This repo contains a reference implementation.
PHP and Scala work on the same folder structure via config params.

### online/index.php
handling the PUT/GET and saving all requests into a databus repo folder structure
Note that the internal handling can be changed, i.e. use a database instead of a folder structure

### Ammonite Scala script
The script is intended as a cronjob and checks whether all downloadURLs are reachable via HEAD requests and saves the stats in tsv files:
```
timestamp	success		downloadurl
```
From the tsv calculates a JSON-LD summary which rates the online availability accoding to the historic data samples it collected, i.e. mostly online

TODO only partially implemented

#### Run
```
sudo amm check_if_online.scala /var/www/html/online/repo
```
