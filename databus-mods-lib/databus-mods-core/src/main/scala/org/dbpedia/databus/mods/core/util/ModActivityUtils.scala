package org.dbpedia.databus.mods.core.util

import org.dbpedia.databus.mods.core.io.DataUtil
import org.dbpedia.databus.mods.core.model.ModActivityRequest

import java.io.InputStream
import java.net.URI

object ModActivityUtils {

  def openInputStream(modActivityRequest: ModActivityRequest): InputStream = {
    modActivityRequest.accessUri match {
      case Some(accessUri) =>
        DataUtil.openStream(new URI(accessUri))
      case None =>
        DataUtil.openStream(new URI(modActivityRequest.dataId))
    }
  }
}
