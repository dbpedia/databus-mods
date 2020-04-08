package org.dbpedia.databus.controller

import org.dbpedia.databus.indexer.Index
import org.springframework.context.support.ClassPathXmlApplicationContext

object SpringExample extends App{


    // open & read the application context file
    val ctx = new ClassPathXmlApplicationContext("applicationContext.xml")
    val i = ctx.getBean("index").asInstanceOf[Index]
    i.updateIndexes()

}
