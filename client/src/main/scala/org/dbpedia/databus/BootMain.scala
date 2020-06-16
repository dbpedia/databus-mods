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
package org.dbpedia.databus

import java.util.concurrent.ConcurrentLinkedQueue

import org.dbpedia.databus.controller.Agent
import org.dbpedia.databus.indexer.Index
import org.springframework.context.support.ClassPathXmlApplicationContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


object BootMain {

  def main(args: Array[String]): Unit = {

    val time = System.currentTimeMillis()
    // open & read the application context file
    val ctx = new ClassPathXmlApplicationContext("applicationContext.xml")
    val i = ctx.getBean("index").asInstanceOf[Index]
    i.updateIndexes()

    // process
    //Execute the extraction jobs one by one

    val jobsRunning = new ConcurrentLinkedQueue[Future[java.io.Serializable]]()
    val maxParallelProcesses = 1
    val iterItem = i.getNewResultSet
    while (iterItem.next) {
      while (jobsRunning.size() >= maxParallelProcesses) {
        Thread.sleep(1000)
      }

      val item = iterItem.getItem
      val agent = ctx.getBean("agent").asInstanceOf[Agent]
      // executes agent.process in a future thread (non blocking)
      val future = Future(agent.process(item, i))
      jobsRunning.add(future)
      future.onComplete {
        case Failure(f) => throw f
        case Success(_) => jobsRunning.remove(future)
      }
    }
    printf(
      s"""
         |maxparallelprocesses ${maxParallelProcesses}

         |time needed: ${System.currentTimeMillis() - time}
                ms

         |""".stripMargin)

  }
}
