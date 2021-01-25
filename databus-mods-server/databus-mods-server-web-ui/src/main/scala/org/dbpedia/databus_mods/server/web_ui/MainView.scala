package org.dbpedia.databus_mods.server.web_ui

import java.util

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.dbpedia.databus_mods.server.core.persistence.{DatabusFile, DatabusFileRepository}
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

@Route("home")
case class MainView @Autowired()(repo: DatabusFileRepository) extends VerticalLayout {

  val grid: Grid[DatabusFile] = new Grid(classOf[DatabusFile])
  grid.setPageSize(20)
  grid.removeColumnByKey("publisher")

  add(grid)
  listCustomers()
  setSizeFull()

  def listCustomers(): Unit = {
    val arr = new util.ArrayList[DatabusFile]()
    repo.findAll().foreach(arr.add)
    grid.setItems(arr)
  }


  //    val items: java.util.List[Person] = new java.util.ArrayList()
  //    items.add(new Person(20,"Peters","foo"));
  //    items.add(new Person(21,"Frank","bar"));
  //    items.add(new Person(21,"Frank3","fuu"));
  //    items.add(new Person(21,"Frank4","baz"))
  //
  //    grid.setItems(items);
  //
  //    add(grid);
  //}
}


