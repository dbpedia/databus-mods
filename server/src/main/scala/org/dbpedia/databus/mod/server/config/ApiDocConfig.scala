package org.dbpedia.databus.mod.server.config

import org.springframework.context.annotation.{Bean, Configuration}
import springfox.documentation.builders.{PathSelectors, RequestHandlerSelectors}
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class ApiDocConfig {

  @Bean
  def api(): Docket = {
    new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.basePackage("org.dbpedia.databus.mod.server"))
      .paths(PathSelectors.any())
      .build()
      .apiInfo(apiInfo)
  }

  def apiInfo: ApiInfo = {
    new ApiInfo(
      "Databus Mod Server",
      "Serves and schedules DBpedia Databus Mod agents",
      "1.0",
      "termsOfServiceUrl",
      "contactName or Concact",
      "license",
      "licenseUrl"
    )
  }
}
