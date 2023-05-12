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
import org.dbpedia.databus.mods.core.worker.execution.{Extension, ModProcessor}

import org.dbpedia.databus.mods.core.worker.AsyncWorker
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(value = Array(classOf[AsyncWorker]))
class Worker {
  
  @Component
  class Process extends ModProcessor {
    
    def process(ext: Extension): Unit = {
      ext.setType("https://mods.tools.dbpedia.org/ns/rdf#SomeMod")
      ext.addPrefix("", "https://mods.tools.dbpedia.org/ns/rdf#")
      val modResultFile = ext.createModResult("file.txt", "http://dataid.dbpedia.org/ns/mods#statisticsDerivedFrom")
      // TODO write into file
    }
  }
}

object Worker extends App {
  SpringApplication.run(classOf[Worker], args: _*)
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

