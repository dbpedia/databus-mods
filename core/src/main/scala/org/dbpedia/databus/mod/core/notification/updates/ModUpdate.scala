package org.dbpedia.databus.mod.core.notification.updates

import scala.beans.BeanProperty

case class ModUpdate(
                      @BeanProperty updates: java.util.List[Process]
                    ) {}
