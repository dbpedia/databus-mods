package org.dbpedia.databus_mods.server.web_ui

import java.io.FileInputStream
import java.net.URL
import java.util.Calendar

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.{HorizontalLayout, VerticalLayout}
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.{ClickEvent, ComponentEventListener}
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.{PageTitle, Route}
import org.apache.jena.datatypes.xsd.XSDDateTime
import org.apache.jena.rdf.model.{ModelFactory, ResourceFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}

import scala.collection.mutable.ListBuffer

@PageTitle("Databus Mods Demo")
@Route("")
case class MainView(@Value("${tmp.base}") baseUrl : String) extends VerticalLayout {

  @Autowired
  private var repo: Repo = _

  @Autowired
  private var vos: Vos = _

  val headline =  new H1("Databus Mods Demo Mod - Example Metadata Overlay")
  add(headline)

  val databusIDLabel = new com.vaadin.flow.component.html.Label("databus file identifier")
  add(databusIDLabel)
  val databusIdTF = new TextField()
  databusIdTF.setWidth("50%")
  databusIdTF.setPlaceholder("https://databus.dbpedia.org/jj-author/mastr/bnetza-mastr/01.04.01/bnetza-mastr_rli_type=wind.nt.gz")
  databusIdTF.setValue("https://databus.dbpedia.org/jj-author/mastr/bnetza-mastr/01.04.01/bnetza-mastr_rli_type=wind.nt.gz")
  add(databusIdTF)

  val annotationUrls: ListBuffer[AnnotationURL] = ListBuffer[AnnotationURL]()
  import scala.collection.JavaConversions._
  val annotationProvider = new ListDataProvider[AnnotationURL](annotationUrls)
  val annotationGrid = new Grid(classOf[AnnotationURL])

  annotationGrid.setWidth("50%")
  annotationGrid.setDataProvider(annotationProvider)
  add(annotationGrid)

  val inputLabel = new com.vaadin.flow.component.html.Label("annotation url")
  add(inputLabel)

  val inputTF = new TextField()
  inputTF.setPlaceholder("http://www.w3.org/2002/07/owl#Thing")
  inputTF.setValue("http://www.w3.org/2002/07/owl#Thing")
  inputTF.setWidth("100%")

  val inputBTN = new Button("+")
  inputBTN.addClickListener(new ComponentEventListener[ClickEvent[Button]] {
    override def onComponentEvent(event: ClickEvent[Button]): Unit = {
      annotationUrls.add(new AnnotationURL(new URL(inputTF.getValue)))
      annotationProvider.refreshAll()
      inputTF.setValue("")
    }
  })
  val inputGroup = new HorizontalLayout(inputTF,inputBTN)
  inputGroup.setWidth("50%")
  add(inputGroup)

  val submitBTN = new Button("submit")
  submitBTN.addClickListener(new ComponentEventListener[ClickEvent[Button]] {
    override def onComponentEvent(event: ClickEvent[Button]): Unit = {

      import org.dbpedia.databus_mods.lib.util.ModelUtil.ModelWrapper

      val databusId = databusIdTF.getValue
      val databusIdPath  = databusId.replace("https://databus.dbpedia.org","")

      val modResult = ModelFactory.createDefaultModel()
      annotationUrls.foreach({
        annotationUrl =>
          modResult.addStmtToModel("result.ttl","http://purl.org/dc/elements/1.1/subject", ResourceFactory.createResource(annotationUrl.url))
      })

      val modMetadata = ModelFactory.createDefaultModel()
      modMetadata.addStmtToModel("mod.ttl","http://www.w3.org/ns/prov#used",ResourceFactory.createResource(databusId))
      modMetadata.addStmtToModel("mod.ttl","http://www.w3.org/ns/prov#generated",ResourceFactory.createResource("result.ttl"))
      modMetadata.addStmtToModel("mod.ttl","http://www.w3.org/ns/prov#startedAtTime",new XSDDateTime(Calendar.getInstance()))
      modMetadata.addStmtToModel("mod.ttl","http://www.w3.org/ns/prov#endedAtTime",new XSDDateTime(Calendar.getInstance()))
      modMetadata.addStmtToModel("mod.ttl","http://www.w3.org/1999/02/22-rdf-syntax-ns#type",ResourceFactory.createResource("http://mods.tools.dbpedia.org/ns/demo/DemoMod"))

      repo.save(modMetadata,databusIdPath,"mod.ttl")
      repo.save(modResult,databusIdPath,"result.ttl")

      val graphName = baseUrl+databusIdPath

      val _modMetadata = ModelFactory.createDefaultModel()
      _modMetadata.read(new FileInputStream(repo.getFile(databusIdPath,"mod.ttl").get),graphName+"/","TURTLE")
      vos.loadModel(graphName,_modMetadata,true)

      val _modResult = ModelFactory.createDefaultModel()
      _modResult.read(new FileInputStream(repo.getFile(databusIdPath,"result.ttl").get),graphName+"/","TURTLE")
      vos.loadModel(graphName,_modResult)

      println("submitted data")
      getUI.get().getPage.setLocation("/repo"+databusIdPath)
    }
  })
  add(submitBTN)

//  val grid: Grid[DatabusFile] = new Grid(classOf[DatabusFile])
//  grid.setPageSize(20)
//  grid.removeColumnByKey("publisher")
//
//  add(grid)
//  listCustomers()
//  setSizeFull()
//
//  def listCustomers(): Unit = {
//    val arr = new util.ArrayList[DatabusFile]()
//    repo.findAll().foreach(arr.add)
//    grid.setItems(arr)
//  }

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


