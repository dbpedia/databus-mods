package org.dbpedia.databus.indexer

import org.springframework.context.support.ClassPathXmlApplicationContext

object SpringExample extends App{


    // open & read the application context file
    val ctx = new ClassPathXmlApplicationContext("applicationContext.xml")
    val i = ctx.getBean("index").asInstanceOf[Index]
    i.updateIndexes()

}
