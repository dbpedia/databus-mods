package org.dbpedia.databus_mods.server.core.utils

import java.sql.Timestamp
import java.text.{ParseException, SimpleDateFormat}

object DateUtil {

  private final val DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  val lol = ""

  def parseToTimestamp(timestamp: String): java.sql.Timestamp = {
    try {
      new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime)
    } catch {
      case e: ParseException => throw new IllegalArgumentException(e);
    }
  }
}
