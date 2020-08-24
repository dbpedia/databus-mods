package org.dbpedia.databus_mods.spo

import org.springframework.context.annotation.{Bean, Configuration}
import springfox.documentation.builders.{PathSelectors, RequestHandlerSelectors}
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc

@Configuration
@EnableSwagger2WebMvc
class ApiDocConfig {

  def apiInfo: ApiInfo = {
    new ApiInfo(
      "Databus Mod Worker",
      "Databus Mod Worker",
      "1.0",
      "termsOfServiceUrl",
      "contactName or Concact",
      "license",
      "licenseUrl"
    )
  }

  @Bean
  def api(): Docket = {
    new Docket(DocumentationType.SWAGGER_2)
      .useDefaultResponseMessages(false)
      .select()
      .apis(RequestHandlerSelectors.basePackage("org.dbpedia.databus_mods.spo"))
      .paths(PathSelectors.any())
      .build()
      .pathMapping("/")
      .apiInfo(apiInfo)
  }
}