package org.dbpedia.databus_mods.server.web_ui

import scala.beans.BeanProperty

class Person(_age: Int, _name: String, _address: String) {

  @BeanProperty
  var age: Int = _age

  @BeanProperty
  var name: String = _name

}
