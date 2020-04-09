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
