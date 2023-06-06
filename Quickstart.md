# Build a Mod

```xml
    <repositories>
        <repository>
            <id>akswnc7</id>
            <name>AKSW NC7 DAV</name>
            <url>https://akswnc7.informatik.uni-leipzig.de/dav/mavenrepository/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.dbpedia.databus-mods</groupId>
            <artifactId>databus-mods-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>        
```

```scala
package org.dbpedia.databus.mods.worker.dummy

import org.dbpedia.databus.mods.core.model.{ModActivity, ModActivityMetadata, ModActivityMetadataBuilder, ModActivityRequest}
import org.dbpedia.databus.mods.worker.springboot.{EnableModWorkerApi, ModWorkerApiProfile}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

import scala.util.Random;

@SpringBootApplication
class DummyWorker {

  @Bean
  @EnableModWorkerApi(version = "1.0.0", profile = ModWorkerApiProfile.HttpPoll)
  def getModActivity: ModActivity = new ModActivity {
    override def perform(request: ModActivityRequest, builder: ModActivityMetadataBuilder): ModActivityMetadata = {
      builder.withStatSummary((Random.nextInt(100)/100.0).toString).build()
    }
  }
}
```

# Deploy the Architecture
> requirements Java 11+

Change application yaml and add mod
```yaml
mods:
  - name: '${modName}'
    accept: 'file'
    select: 
    workers:
      - 'http://localhost:${startPort}-${endPort}/modApi'
    query: '
PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>
PREFIX dct:    <http://purl.org/dc/terms/>

  SELECT DISTINCT ?file ?issued ?downloadURL ?sha256sum WHERE {
  ?dataset a dataid:Dataset ;
  dcat:distribution ?part .
  ?part dataid:file ?file ;
  dataid:sha256sum ?sha256sum ;
  dct:issued ?issued ;
  dcat:downloadURL ?downloadURL .
}
'
```

Run server/master node
```bash
mvn spring-boot:run
```

