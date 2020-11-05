# Databus-Mods

## Run in Intellij

1. add Java Application Launcher
2. change working directory to Mod subdirectory
3. set MainClass (e.g., Boot.scala)

## Mod ReST Api

Example [DemoMod](http://akswnc7.informatik.uni-leipzig.de:9010/swagger-ui/) documentation.

Request path `/${publisher}/${group}/${artifact}/${version}/${file}`

| Status Code | Description | Response |
|-------------|-------------|----------|
| 200 | mod result | turtle/turtle |
| 202 | accepted but pending | NULL / Location|
| 400 | bad request, e.g., parameters | NULL |
| 500 | internal server error | NULL |

## Mod Result

> cf., README.md

```turtle
@prefix dataid-mt: <http://dataid.dbpedia.org/ns/mt#> .
@prefix mod:   <http://dataid.dbpedia.org/ns/mod.ttl#> .
@prefix dcat:  <http://www.w3.org/ns/dcat#> .
@prefix prov:  <http://www.w3.org/ns/prov#> .

<file:///absolute/base/dir/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/spo.csv>
        mod:resultDerivedFrom  <https://databus.dbpedia.org/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt> .

<file:///absolute/base/dir/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/mod.ttl#this>
        a                 <file:///absolute/base/dir/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/modvocab.ttl#SPOMod> ;
        prov:endedAtTime  "2020-11-02T18:40:57.69Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
        prov:generated    <file:///absolute/base/dir/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt/spo.csv> ;
        prov:used         <https://databus.dbpedia.org/ontologies/w3.org/ns--dcat/2020.06.10-215528/ns--dcat_type=parsed.nt> .

```

## Build a Mod

### Spring and Databus-Mod-Lib

**TODO: release org.dbpedia.databus-mods:databus-mods-parent** 

```scala
@SpringBootApplication
@EnableAutoConfiguration
class Boot

object Boot {

  @Configuration
  class DatabusModConfig extends AbcDatabusModConfig

  @Bean
  def getQueue: DatabusModInputQueue = new DatabusModInputQueue

  @Controller
  class DatabusModController @Autowired()(config: DatabusModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModController(config, queue)

  @Component
  class DatabusModProcessor @Autowired()(config: DatabusModConfig, queue: DatabusModInputQueue)
    extends AbcDatabusModProcessor(config, queue) {
    override def process(input: DatabusModInput): Unit = {
      /* TODO */
    }
  }

  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Boot], args: _*)
  }
}
```

#### Docker

The easiest way to deploy Spring-based Databus Mod is to use docker.
```
mvn spring-boot:build-image
```

Run mod and set memory
```
docker run -m 8g docker.io/library/${artifact}:${version}
# docker run -m 8g docker.io/library/databus-mods-void:1.0-SNAPSHOT
```

## Testing

```
curl -v \
   --data-urlencode 'fileUri=file:///absolute/path/to/file' \
  'http://localhost:9001/publisher/group/artifact/version/file'
```

Or use
```
cd databus-mods-server

query=/path/to/query
modUrl=http://example.org/api/url
hostCachePath=/abs/path/on/master/
modCachePath=/abs/path/on/worker/

mvn exec:java -Dexec.mainClass="org.dbpedia.databus_mods.server.cli.ServerCLI" -Dexec.args="$query $modUrl $hostCachePath $modCachePath"
```

## Known issues

Issue
```
org.dbpedia.databus-mods:databus-mods-lib not found
```
Solution
```
mvn clean install
```
