# Databus-Mods

## Run in Intellij

1. add Java Application Launcher
2. change working directory to Mod subdirectory
3. set MainClass (e.g., Boot.scala)

## Known issues

Issue
```
org.dbpedia.databus-mods:databus-mods-lib not found
```
Solution
```
mvn clean install
```
```
curl -v --data-urlencode 'fileUri=file:///local/path/shared/volume/to/' 'http://localhost:9001/a/dbpedia/generic/labels/2020.06.01/labels_lang=de.ttl.bz2'
```

## How to build a Mod 

### Requirements 

ReST API
SPARQL
File Server