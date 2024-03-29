# Databus Mods [![Java 11 CI with Maven](https://github.com/dbpedia/databus-mods/actions/workflows/maven-java-17.yml/badge.svg)](https://github.com/dbpedia/databus-mods/actions/workflows/maven-java-17.yml)
Description and Demo for Databus Mods (incl/ Ontology and Process)
Contains a working mod to check whether all download links are working.
Please post ideas and endeavours to mod in the [DBpedia Forum](https://forum.dbpedia.org) 

The **documentation** can be viewed as [Gitbook](https://dbpedia.gitbook.io/databus/v/mods/overview/readme)

and for **full detailed information** in the master's thesis of Marvin Hofer ["Databus Mods - Linked Data-driven Enrichment of Metadata"](https://svn.aksw.org/papers/2021/databus-mods-thesis/public.pdf)

## Structure

The project contains the following modules:
* [`databus-mods-lib`](databus-mods-lib) core implementation, shared libraries, and utilities
* [`databus-mods-server`](databus-mods-server) reusable server implementation
* [`databus-mods`](databus-mods) basic Databus Mod Worker implementations and examples

## Test Deployment
```bash
docker-compose up
```
visit [localhost:8080](localhost:8080)

## License
