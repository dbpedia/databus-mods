package org.dbpedia.databus.mod.core.notification.subscription

import scala.beans.BeanProperty

class ModSubRequest(
                     @BeanProperty name: String,
                     @BeanProperty query: String,
                     @BeanProperty need: String
                   ) {}
