//package org.dbpedia.databus_mods.server.core.databus
//
//import java.util.concurrent.{LinkedBlockingDeque, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}
//import java.util.function.Consumer
//
//import org.dbpedia.databus_mods.server.core.persistence.{DatabusFileRepository, Mod, ModRepository, TaskRepository}
//import org.spark_project.jetty.util.ConcurrentArrayQueue
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.CommandLineRunner
//import org.springframework.stereotype.Component
//import java.util.{Map => JMap, TreeMap => JTreeMap}
//
//import org.slf4j.{Logger, LoggerFactory}
//
//import scala.util.Random
//
//@Component
//class Scheduler extends CommandLineRunner {
//
//  private val log: Logger = LoggerFactory.getLogger(classOf[Scheduler])
//
//  @Autowired private var modRepository: ModRepository = _
//
//  @Autowired private var taskRepository: TaskRepository = _
//
////  @Autowired
////  private var databusFileRepository: DatabusFileRepository = _
////
////  override def run(args: String*): Unit = {
////
////
////  }
//
////  private val map: JMap[String,Dispatcher] = new JTreeMap[String,Dispatcher]()
////
////  @Autowired
////  private var apis: ApiQueue = _
////
////  private val queue = new LinkedBlockingQueue[Runnable]()
//
////  override def run(args: String*): Unit = {
////
////    log.info("init")
////
////    new Thread(new Runnable {
////      override def run(): Unit = {
////
////      }
////    })
//
////    new Thread(new Runnable {
////      override def run(): Unit = {
////        while (true) {
////          Thread.sleep(1000)
////          modRepository.findAll().forEach(new Consumer[Mod] {
////            override def accept(t: Mod): Unit = println(t.info)
////          })
////        }
////      }
////    }).start()
//
////    val es = new ThreadPoolExecutor(4,4,0,TimeUnit.MILLISECONDS,queue)
//
////    while (true) {
////
////      val api = apis.take()
////      es.submit(new Runnable {
////        override def run(): Unit = {
////          println("request "+api)
////          val time = (Random.nextInt(4)+1)*500
////          Thread.sleep(time)
////          println("request "+api+" took "+time)
////          apis.add(api)
////        }
////      })
////    }
////  }
//}
