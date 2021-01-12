package org.dbpedia.databus_mods.void

import org.dbpedia.databus_mods.void.gtd.Repo
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableAutoConfiguration
class Boot {

  private val log: Logger = LoggerFactory.getLogger(classOf[Boot])

  @Autowired var config: Config = _

  @Bean
  def getRepo: Repo = {
    new Repo(config.worker.volume)
  }
}

object Boot extends App {

  val app: ConfigurableApplicationContext = SpringApplication.run(classOf[Boot], args: _*)

//  val executor: DatabusModExecutor = app.getBean(classOf[DatabusModExecutor])
//  new Thread(executor).run()
}
