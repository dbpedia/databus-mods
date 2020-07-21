package org.dbpedia.databus_mods.server

import java.util.{ArrayList => JArrayList}

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class Config() {
  @BeanProperty
  var mods : JArrayList[ModConfig] = new JArrayList[ModConfig]()

  @BeanProperty
  var volumes: VolumeConfig = new VolumeConfig
}

case class ModConfig() {
  @BeanProperty
  var name: String = _

  @BeanProperty
  var accepts: String = _

  @BeanProperty
  var links: JArrayList[String] = new JArrayList[String]()

  @BeanProperty
  var query: String = _
}

case class VolumeConfig () {
  @BeanProperty
  var localRepo: String = _
}