package org.dbpedia.databus_mods.lib

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class AbcDatabusModConfig() {

  @BeanProperty
  var name: String = _

  @BeanProperty
  var volumes: VolumesConfig = new VolumesConfig

  case class VolumesConfig() {

    @BeanProperty
    var fileCache: String = _

    @BeanProperty
    var localRepo: String = _
  }
}
