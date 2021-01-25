package org.dbpedia.databus_mods.server.utils

import java.sql.Timestamp
import java.text.{DateFormat, ParseException, ParsePosition, SimpleDateFormat}

object DateUtil {

  private final val DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def parseToTimestamp(timestamp: String): java.sql.Timestamp = {
    try {
      new Timestamp(DATE_TIME_FORMAT.parse(timestamp).getTime)
    } catch {
      case e: ParseException => throw new IllegalArgumentException(e);
    }
  }
}
