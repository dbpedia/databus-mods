//package org.dbpedia.databus_mods.void
//
//import org.springframework.context.annotation.{Bean, Configuration}
//
//@Configuration
//@EnableSwagger2WebMvc
//class ApiDocConfig {
//
//  def apiInfo: ApiInfo = {
//    new ApiInfo(
//      "Databus Mod Worker",
//      "Databus Mod Worker",
//      "1.0",
//      "termsOfServiceUrl",
//      "contactName or Concact",
//      "license",
//      "licenseUrl"
//    )
//  }
//
//  @Bean
//  def api(): Docket = {
//    new Docket(DocumentationType.SWAGGER_2)
//      .useDefaultResponseMessages(false)
//      .select()
//      .apis(RequestHandlerSelectors.basePackage("org.dbpedia.databus_mods.void"))
//      .paths(PathSelectors.any())
//      .build()
//      .pathMapping("/")
//      .apiInfo(apiInfo)
//  }
//}