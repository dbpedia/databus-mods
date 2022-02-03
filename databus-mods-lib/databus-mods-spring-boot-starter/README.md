# Building Databus Mods with Spring Boot

The Databus Mods Spring addon allows you to build Databus Mods with Spring Boot.

```java
@SpringBootApplication
@Import({org.dbpedia.databus_mods.lib.worker.PollingAPIWorker.class})
public class Boot {

  @Component
  public class MyModActivity implements ModActivity {

    public void perform(ModActivityBuilder mab) {
      extension.setType("http://my.domain/ns#DatabusMod");
      val sourceURI = mab.getSourceURI();
      // TODO process data from sourceURI
      File modResultFile = extension.createModResult("modresult.extension");
      // TODO write to modResultFile
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(Boot.class);
  }
}
```
