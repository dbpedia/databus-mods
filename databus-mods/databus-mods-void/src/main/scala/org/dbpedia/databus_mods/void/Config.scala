package org.dbpedia.databus_mods.void

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty

@Configuration
@ConfigurationProperties
case class Config() {

  @BeanProperty
  var name: String = _

  @BeanProperty
  var worker: WorkerConfig = new WorkerConfig
}

case class WorkerConfig() {

  @BeanProperty
  var volume: String = _

  @BeanProperty
  var api: ApiConfig = new ApiConfig
}

case class ApiConfig() {

  @BeanProperty
  var base: String = _

  @BeanProperty
  var create: String = _

  @BeanProperty
  var query: String = _
}

