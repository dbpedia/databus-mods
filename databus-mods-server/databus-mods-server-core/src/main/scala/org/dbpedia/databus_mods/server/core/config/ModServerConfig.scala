package org.dbpedia.databus_mods.server.core.config

import java.util.{ArrayList => JArrayList}

import org.dbpedia.databus_mods.server.core.config.mods.ModConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class ModServerConfig
(
  @BeanProperty
  var mods: JArrayList[ModConfig] = new JArrayList[ModConfig]()
)
