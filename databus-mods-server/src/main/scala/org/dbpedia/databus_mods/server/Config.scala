package org.dbpedia.databus_mods.server

import java.util.{ArrayList => JArrayList}

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class Config() {
  @BeanProperty
  var mods: JArrayList[ModConfig] = new JArrayList[ModConfig]()

  @BeanProperty
  var database: DatabaseConfig = new DatabaseConfig

  @BeanProperty
  var fileCache: FileCacheConfig = new FileCacheConfig

  @BeanProperty
  var extServer: ExtServerConfig = new ExtServerConfig
}

case class ExtServerConfig() {

  @BeanProperty
  var sparql: ExtServerSparqlConfig = new ExtServerSparqlConfig

  @BeanProperty
  var http: ExtServerHttpConfig = new ExtServerHttpConfig
}

case class ExtServerSparqlConfig() {

  @BeanProperty
  var databaseUrl: String = _

  @BeanProperty
  var databaseUsr: String = _

  @BeanProperty
  var databasePsw: String = _

  @BeanProperty
  var endpoint: String = _
}

case class ExtServerHttpConfig() {

  @BeanProperty
  var baseUrl: String = _

  @BeanProperty
  var volume: String = _
}

case class FileCacheConfig() {

  @BeanProperty
  var maxNumberOfFiles: Int =  _

  @BeanProperty
  var volume: String = _
}

case class DatabaseConfig() {
  @BeanProperty
  var databaseUrl: String = _
}

case class ModConfig() {
  @BeanProperty
  var name: String = _

  @BeanProperty
  val load: JArrayList[String] = new JArrayList[String]()

  @BeanProperty
  var accepts: String = _

  @BeanProperty
  var links: JArrayList[LinkConfig] = new JArrayList[LinkConfig]()

  @BeanProperty
  var query: String = _
}

case class LinkConfig() {
  @BeanProperty
  var api: String = _

  @BeanProperty
  var fileCache: String = _

  @BeanProperty
  var localRepo: String = _

  @BeanProperty
  var mountRepo: String = _
}