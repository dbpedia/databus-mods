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
package org.dbpedia.databus.controller

import org.dbpedia.databus.indexer.Index
import org.springframework.context.support.ClassPathXmlApplicationContext

object ControllerMain extends App{


    // open & read the application context file
    val ctx = new ClassPathXmlApplicationContext("applicationContext.xml")
    val i = ctx.getBean("index").asInstanceOf[Index]
    i.updateIndexes()


    // process
    val iterItem = i.getNewResultSet
    while (iterItem.next) {
        val item = iterItem.getItem
        val agent = ctx.getBean("agent").asInstanceOf[Agent]
        agent.process(item)
    }
}
