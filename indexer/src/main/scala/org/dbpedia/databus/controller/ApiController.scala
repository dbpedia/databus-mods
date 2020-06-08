package org.dbpedia.databus.controller

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PutMapping, RequestMapping, RestController}


@RestController
@EnableAutoConfiguration
@RequestMapping(Array("/api"))
class ApiController {

  @GetMapping(value = Array("", "/", "/info"))
  def info(): String = {
    """
      |TODO: include static pages or use template engine (thymeleaf)
      |""".stripMargin
  }

  @GetMapping(value = Array("mod/{account}/{group}/{artifact}/{version}"))
  def stats(
             @PathVariable account: String,
             @PathVariable group: String,
             @PathVariable artifact: String,
             @PathVariable version: String
           ): ResponseEntity[String] = {

    val notFound = true
    if (notFound)
      new ResponseEntity[String](s"$artifact $group $artifact $version", HttpStatus.NOT_FOUND)
    else
      new ResponseEntity[String](s"$artifact $group $artifact $version", HttpStatus.OK)
  }

  @GetMapping(value = Array("mod/{id}"))
  def view(@PathVariable id: String): ResponseEntity[String] = {

    val exists = true
    if (exists)
      new ResponseEntity[String](s"GET $id", HttpStatus.OK)
    else
      new ResponseEntity[String](s"GET $id", HttpStatus.NOT_FOUND)
  }

  @PutMapping(value = Array("mod/{id}"))
  def create(@PathVariable id: String): ResponseEntity[String] = {

    val exists, accepted = true
    if (exists)
      new ResponseEntity[String](s"PUT $id", HttpStatus.NOT_MODIFIED)
    else if (accepted)
      new ResponseEntity[String](s"PUT $id", HttpStatus.CREATED)
    else
      new ResponseEntity[String](s"PUT $id", HttpStatus.NOT_ACCEPTABLE)
  }
}
