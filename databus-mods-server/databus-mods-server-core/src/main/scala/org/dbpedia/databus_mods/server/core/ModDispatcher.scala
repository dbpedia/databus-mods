package org.dbpedia.databus_mods.server.core

import java.net.{URI, URL}
import java.util.concurrent.{CompletableFuture, LinkedBlockingQueue, TimeUnit}
import java.util.function.{Consumer, Supplier}

import org.apache.jena.rdf.model.Model
import org.apache.jena.riot.Lang
import org.apache.spark.sql.catalyst.expressions.Rand
import org.apache.tomcat.util.threads.ThreadPoolExecutor
import org.dbpedia.databus_mods.server.core.persistence.{Mod, ModRepository, TaskStatus, Task, TaskRepository}

import scala.util.Random

class ModDispatcher(taskRepository: TaskRepository,vosService: VosService) extends Runnable {

  var mod: Mod = _

  def setMod(mod: Mod): Unit = {
    this.mod = mod
  }

  def update(): Unit = {
    synchronized {
      val newTasks = taskRepository.findByModNameAndStateOrderByDatabusFileIssuedDesc(mod.name,TaskStatus.Open.id)
      newTasks.forEach(new Consumer[Task] {
        override def accept(t: Task): Unit = {
          t.setState(TaskStatus.Wait.id)
          taskRepository.save(t)
          tasks.add(t)
        }
      })
    }
  }

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  def this(mod: Mod, taskRepository: TaskRepository,vosService: VosService) {
    this(taskRepository,vosService)
    this.mod = mod
    this.balanceQueue.addAll(mod.worker.map(_.addr))
  }

  val connectionPool = new ThreadPoolExecutor(1,10,0,TimeUnit.MILLISECONDS,new LinkedBlockingQueue[Runnable]())

  val balanceQueue = new LinkedBlockingQueue[String]()

  val tasks = new LinkedBlockingQueue[Task]()

  override def run(): Unit = {

    while (true) {
      val task = tasks.take()
      CompletableFuture
        .supplyAsync[Model](new TaskHandler(task,balanceQueue),connectionPool)
        .thenAcceptAsync(new Consumer[Model] {
          override def accept(t: Model): Unit = {
            val name = s"http://named.org/${Random.nextInt(10000)}"
            println(s"add $name")
            try {
              vosService.addNamedModel(name,t)
            } catch {
              case exception: Exception => exception.printStackTrace()
            }
          }
        })
    }
  }
}
