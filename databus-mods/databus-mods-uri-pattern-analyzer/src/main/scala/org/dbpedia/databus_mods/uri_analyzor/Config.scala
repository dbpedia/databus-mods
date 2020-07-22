package org.dbpedia.databus_mods.uri_analyzor

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class Config() {

  @BeanProperty
  var name: String = _

  @BeanProperty
  var volumes = new VolumesConfig
}

case class VolumesConfig() {

  @BeanProperty
  var fileCache: String = _

  @BeanProperty
  var localRepo: String = _
}

