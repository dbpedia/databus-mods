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
  var volumes: VolumeConfig = new VolumeConfig

  @BeanProperty
  var database: DatabaseConfig = new DatabaseConfig
}

case class DatabaseConfig() {
  @BeanProperty
  var databaseUrl: String = _
}

case class ModConfig() {
  @BeanProperty
  var name: String = _

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
  var source: String = _

  @BeanProperty
  var destination: String = _
}

case class VolumeConfig()
{
  @BeanProperty
  var localRepo: String = _
}