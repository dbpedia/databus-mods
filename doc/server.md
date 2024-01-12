# Quickstart Server

Quickstart guide to configure and run Databus Mods Server.

## Prerequisites 
- Java: > 17
- Maven: > 3.8.4 
- Storage
  - RDF Store and Sparql Server 
  - Http Web Server

## Setup

The Databus Mods Server can run natively with local installed Maven and Java or inside docker.

## Install

```bash
git clone $THIS_REPO
cd databus-mods
mvn clean compile
```

## Configure

As the server is implemented with Spring Boot, all configuration paramters are defined in a spring application configuration file `application.properties` or `application.yml`.

```yaml
# Minimal example application yml

```

## Run with Maven and Java

## Run with Docker-Compose

### Build Image

```bash
docker built -t dbpedia/databus-mods-server . 
```

### Start Containers

```yaml
version: '3'

services:
  server:
    image:

# todo https://github.com/docker/compose/issues/1896
```

