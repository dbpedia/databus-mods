package org.dbpedia.databus_mods.server.core.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.HttpMethod
import springfox.documentation.builders.{PathSelectors, RequestHandlerSelectors}
import springfox.documentation.service.{ApiInfo, Contact, Response, VendorExtension}
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class ApiDocumentation {
    def apiInfo: ApiInfo = {
      new ApiInfo(
        "Databus Mod Server",
        "Databus Mod Server",
        "1.0",
        "#",
        new Contact("Marvin Hofer", "http://aksw.org/MarvinHofer.html", "hofer (at) infai.org"),
        "License pending...",
        "#",
        new java.util.ArrayList[VendorExtension[_]]()
      )
    }

    @Bean
    def api: Docket = {

      new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("org.dbpedia.databus_mods.server.core.controller"))
        .paths(PathSelectors.any())
        .build()
        .globalResponses(HttpMethod.POST, new java.util.ArrayList[Response]())
        .apiInfo(apiInfo)
    }
}