//package org.dbpedia.databus_mods.server.core
//
//import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap, LinkedBlockingQueue, ThreadPoolExecutor, TimeUnit}
//import java.util.function.Consumer
//
//import org.dbpedia.databus_mods.server.core.persistence.{Mod, TaskRepository}
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
//@Service
//class ModService(
//                  taskRepository: TaskRepository,
//                  vosService: VosService) {
//
//  private val dispatchers: ConcurrentMap[String, ModDispatcher] = new ConcurrentHashMap[String, ModDispatcher]()
//
//  def addModDispatcher(mod: Mod): Unit = {
//    val modDispatcher = new ModDispatcher(mod, taskRepository,vosService)
//    val thread = new Thread(modDispatcher)
//    thread.start()
//    dispatchers.put(mod.name, modDispatcher)
//  }
//
//  def update(mod: Mod): Unit = {
//    val modDispatcher = dispatchers.get(mod.name)
//    modDispatcher.setMod(mod)
//  }
//
//  def notifyDispatcher(): Unit = {
//    dispatchers.values().forEach(new Consumer[ModDispatcher] {
//      override def accept(t: ModDispatcher): Unit = {
//        t.update()
//      }
//    })
//  }
//
//  //  def removeModDispatcher(mod: Mod): Unit = {
//  //    val thread = dispatchers.get(mod.name)
//  //    thread.
//  //  }
//}
