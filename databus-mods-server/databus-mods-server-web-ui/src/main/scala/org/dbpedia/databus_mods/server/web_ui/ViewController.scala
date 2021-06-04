package org.dbpedia.databus_mods.server.web_ui

import java.io.FileInputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Optional

import io.swagger.annotations.ApiOperation
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RequestParam}

@Controller
@RequestMapping(value = Array("repo"))
class ViewController(@Value("${tmp.base}") baseUrl: String) {

  @Autowired
  var repo: Repo = _

  @RequestMapping(
    value = Array(
      "",
      "{account}",
      "{account}/{group}",
      "{account}/{group}/{artifact}",
      "{account}/{group}/{artifact}/{version}",
      "{account}/{group}/{artifact}/{version}/{distribution}",
      "{account}/{group}/{artifact}/{version}/{distribution}/{result}"),
    method = Array(RequestMethod.GET))
  def put(
           @PathVariable account: Optional[String],
           @PathVariable group: Optional[String],
           @PathVariable artifact: Optional[String],
           @PathVariable version: Optional[String],
           @PathVariable distribution: Optional[String],
           @PathVariable result: Optional[String],
           request: HttpServletRequest,
           response: HttpServletResponse)
  : Unit = {

    if (result.isPresent) {
      val dabuIdPath = s"/${account.get()}/${group.get()}/${artifact.get()}/${version.get()}/${distribution.get()}"
      val file = repo.getFile(dabuIdPath, result.get())
      file match {
        case Some(f) =>
          response.setStatus(200)
          response.setHeader("Content-Type", "text/plain")
          IOUtils.copy(new FileInputStream(f), response.getOutputStream)
        case None =>
          response.setStatus(404)
      }
    } else {
      val path = List(account, group, artifact, version, distribution).flatMap({
        part => if (part.isPresent) Some(part.get) else None
      }).mkString("/")
      val html = new StringBuilder("<html>")
      html.append("<h2>Created DataID Extension (Annotation Mod Example)</h2>")
      html.append("<hr>")
      if(distribution.isPresent) {
        html.append("<h3>SPARQL Queries</h3>")
        val query =
          s"""SELECT * FROM <$baseUrl/$path> {
             |  ?s a <http://mods.tools.dbpedia.org/ns/demo/DemoMod> .
             |  ?s ?p ?o .
             |}
             |""".stripMargin
        val href = "https://databus.dbpedia.org/yasgui/#" +
          "query=" + URLEncoder.encode(query, "UTF-8") +
          "&endpoint=" + URLEncoder.encode("https://mods.tools.dbpedia.org/sparql", "UTF-8")
        val query2 =
          s"""SELECT ?file ?annotation {
             |  ?s a <http://mods.tools.dbpedia.org/ns/demo/DemoMod> .
             |  ?s <http://www.w3.org/ns/prov#used> ?file .
             |  ?s <http://www.w3.org/ns/prov#generated> ?g .
             |  ?g <http://purl.org/dc/elements/1.1/subject> ?annotation .
             |  { SELECT ?annotation {
             |  GRAPH <$baseUrl/$path> { ?s2 <http://purl.org/dc/elements/1.1/subject> ?annotation }
             |  }}
             |}
             |""".stripMargin
        val href2 = "https://databus.dbpedia.org/yasgui/#" +
          "query=" + URLEncoder.encode(query2, "UTF-8") +
          "&endpoint=" + URLEncoder.encode("https://mods.tools.dbpedia.org/sparql", "UTF-8")
        html.append("<ul>")
        html.append(s"<li><a href='$href'>Query DataID Extension on Mod Server SPARQL Endpoint</a></li>")
        html.append(s"<li><a href='$href2'>Query other files with same annotations</a></li>")
        html.append("</ul>")
      }
      html.append("<h3>Created Files</h3>")

      /*
      https://databus.dbpedia.org/yasgui/#query=PREFIX+dataid%3A+%3Chttp%3A%2F%2Fdataid.dbpedia.org%2Fns%2Fcore%23%3E%0APREFIX+dct%3A++++%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0APREFIX+dcat%3A+++%3Chttp%3A%2F%2Fwww.w3.org%2Fns%2Fdcat%23%3E%0APREFIX+db%3A+++++%3Chttps%3A%2F%2Fdatabus.dbpedia.org%2F%3E%0APREFIX+rdf%3A++++%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0APREFIX+rdfs%3A+++%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0A%0ASELECT+DISTINCT+%3Fs+WHERE+%7B%0A++%3Fs+a+%3Fp%0A%7D+%0ALIMIT+10&endpoint=https%3A%2F%2Fmods.tools.dbpedia.org%2Fsparql&requestMethod=POST&tabTitle=Query+2&headers=%7B%7D&outputFormat=table
       */

      html.append("<ul>")
      repo.listFiles(path).foreach({
        file =>
          html.append(s"""<li><a href="/repo/$path/${file.getName}">${file.getName}</a></li>""")
      })
      html.append("</ul></html>")
      IOUtils.write(html.toString(), response.getOutputStream, StandardCharsets.UTF_8)
      response.setStatus(200)
    }
  }
}
