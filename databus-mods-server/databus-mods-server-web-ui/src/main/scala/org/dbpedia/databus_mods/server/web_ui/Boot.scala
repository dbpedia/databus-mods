package org.dbpedia.databus_mods.server.web_ui

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.{ComponentScan, Import}
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
//@ComponentScan(basePackages = Array("org.dbpedia.databus_mods.server.core", "package org.dbpedia.databus_mods.server.web_ui"))
//@EnableJpaRepositories(basePackages = Array("org.dbpedia.databus_mods.server.core.persistence"))
//@EntityScan(basePackages =  Array("org.dbpedia.databus_mods.server.core.persistence"))
class Boot {

}

object Boot extends App {
  SpringApplication.run(classOf[Boot], args: _*)
}
