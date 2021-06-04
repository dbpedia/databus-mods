package org.dbpedia.databus_mods.server.web_ui

import java.net.URL

import scala.beans.BeanProperty

class AnnotationURL(_url: URL) {

  @BeanProperty
  var url: String = _url.toString
}
