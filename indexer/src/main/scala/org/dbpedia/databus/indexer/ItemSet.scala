/*-
 * #%L
 * Indexing the Databus
 * %%
 * Copyright (C) 2018 - 2020 Sebastian Hellmann (on behalf of the DBpedia Association)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.dbpedia.databus.indexer

import java.net.URL
import java.sql.ResultSet

/**
 * decorating resultset
 *
 * @param rs
 */
class ItemSet(val rs: ResultSet) {

  def next: Boolean = {
    rs.next()
  }

  def getItem: Item = {

    new Item(
      rs.getString("shasum"),
      new URL(rs.getString("downloadURL")),
      new URL(rs.getString("dataset")),
      new URL(rs.getString("version")),
      new URL(rs.getString("distribution"))
    )
  }

  def close: Unit = {
    rs.close()
  }

}
