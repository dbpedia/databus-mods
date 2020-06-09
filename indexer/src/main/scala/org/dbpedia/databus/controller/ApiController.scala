/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.controller

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._


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

//  @GetMapping(value = Array("mod/test"))
//  def test(): ResponseEntity[Person] = {
//    new ResponseEntity[Person](Person("a","b",21), HttpStatus.OK)
//  }
}
