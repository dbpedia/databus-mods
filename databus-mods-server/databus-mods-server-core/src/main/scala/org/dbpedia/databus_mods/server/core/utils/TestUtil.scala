package org.dbpedia.databus_mods.server.core.utils

import java.util.Calendar

object TestUtil extends App {

  object Util {

    def  doSomething() {
      synchronized {
        Thread.sleep(3000)
        println(s"${Thread.currentThread().getName} ${Calendar.getInstance().getTime}")
      }
    }

    def doSomethingDifferent(): Unit = {
      synchronized {
//        Thread.sleep(1000)
        println(s"${Thread.currentThread().getName} ${Calendar.getInstance().getTime}")
      }
    }
  }

  val t1: Thread = new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        Util.doSomething()
        Thread.sleep(0,1)
//        Thread.sleep(100)
      }
    }
  })
  t1.setName("A")
  t1.start()

  new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        Util.doSomethingDifferent()
//        Thread.sleep(100)
      }
    }
  }).start()
}
