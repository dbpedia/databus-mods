import scala.io.Source
import java.io.File

import org.apache.http.client.methods.HttpHead
import org.apache.http.impl.client.HttpClients
import java.io.FileWriter
import java.time.{Instant, ZoneId, ZonedDateTime}

object check_if_online {

  def main(args: Array[String]): Unit = {
    val updates = "online-updates.tsv"

    println(args(0))
    println(args(1))
    val repo = args(0)
    val serviceRepoURL = args(1)

    //reset aggregate
    writefile(s"$repo/aggregate.nt","",false)

    val modVocab =
      s"""
         |# no base
         |@prefix mod: <http://dataid.dbpedia.org/ns/mod.ttl#> .
         |@prefix owl: <http://www.w3.org/2002/07/owl#>.
         |@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.
         |@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.
         |
         |<#OnlineTestMod> a owl:Class ;
         |  rdfs:subClassOf mod:DatabusMod ;
         |  rdfs:label "Online Stats of dcat:downloadURL" ;
         |  rdfs:comment "Sends daily HEAD requests and logs them in a .tsv file (time, success/failure, url) and calculates a rating." .
         |
         |
         |<onlinerate> a owl:DatatypeProperty ;
         |  rdfs:subPropertyOf mod:statSummary ;
         |  rdfs:label "online rate" ;
         |  rdfs:comment "A daily head request is sent to dcat:downloadURL. Online rate is the percentage of success over all test requests" ;
         |  rdfs:range xsd:float .
       """.stripMargin

    writefile(s"$repo/modvocab.ttl", modVocab, false)

    var first = true
    val bufferedSource = io.Source.fromFile(updates)
    for (line <- bufferedSource.getLines) {
      if (first) {
        first = false
      } else {
        val split = line.split("\t")
        val file = split(0).trim.replace("\"", "")
        val sha = split(1).trim.replace("\"", "")
        val downloadURL = split(2).trim.replace("\"", "")

        // path
        val tmp = file.replace("https://databus.dbpedia.org/", "")
        val pos = tmp.lastIndexOf("/")
        val path = tmp.substring(0, pos)
        val filename = tmp.substring(pos + 1)

        new File(s"$repo/$path/").mkdirs
        //println(path)

        // check
        val success = check(downloadURL)
        //write stats
        writeStats(s"$repo/$path/$sha", success, downloadURL)


        //write svg
        val successrate = getSuccessRate(s"$repo/$path/$sha.tsv")
        writeSVG(s"$repo/$path/$sha.svg", successrate)

        // write HTML Summary
        writeHTMLSummary(s"$repo/$path/$sha.html", s"$repo/$path/$sha.htmltable")

        //write ttl
        writeActivityTTL(s"$repo/$path/$sha.ttl", file, successrate, serviceRepoURL, path, sha, repo)
      }
    }
    bufferedSource.close
  }

  def writeHTMLSummary ( htmlFile:String, statFile : String) ={
    val header =
      s"""
         |<html>
         |<body>
         |<table style="width:100%">
         |  <tr>
         |    <th>Time</th>
         |    <th>Success</th>
         |    <th>dcat:downloadURL</th>
         |  </tr>
         |
       """.stripMargin
    writefile(htmlFile,header,false);

    val bufferedSource = io.Source.fromFile(statFile)

    var content = ""
    for (line <- bufferedSource.getLines) {
      content += line+"n"
    }
    bufferedSource.close

    writefile(statFile,content,true);

    val footer =
      s"""
         |
         |</table>
         |</body>
         |</html>
       """.stripMargin
    writefile(htmlFile,footer,true);

  }

  def writeActivityTTL(activityFile: String, databusfile: String, successrate: Float, serviceRepoURL: String, path: String, sha: String, repo: String) {
   // println(s"$serviceRepoURL/$path/$sha.svg")
   // writefile(summaryfile, jsonld, false)
    val invocationTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
    val ntriples =
      s"""
         |<$serviceRepoURL/$path/$sha.svg> <http://dataid.dbpedia.org/ns/mod.ttl#svgDerivedFrom> <${databusfile}> .
         |<$serviceRepoURL/$path/$sha.html> <http://dataid.dbpedia.org/ns/mod.ttl#htmlDerivedFrom> <${databusfile}> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <http://www.w3.org/ns/prov#generated> <$serviceRepoURL/$path/$sha.svg> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <http://www.w3.org/ns/prov#generated> <$serviceRepoURL/$path/$sha.html> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <http://www.w3.org/ns/prov#endedAtTime> "$invocationTime"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <$serviceRepoURL/modvocab.ttl#OnlineTestMod> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <$serviceRepoURL/modvocab.ttl#onlinerate> "$successrate"^^<http://www.w3.org/2001/XMLSchema#float> .
         |<$serviceRepoURL/$path/$sha.ttl#this> <http://www.w3.org/ns/prov#used> <${databusfile}> .
         |""".stripMargin

   // removed used
   //

    writefile(activityFile, ntriples,false)
    writefile(s"$repo/aggregate.nt", ntriples, true)
  }

  def check(downloadURL: String): Boolean = {
    // do the stats
    val httpclient = HttpClients.createDefault();
    val httpHead = new HttpHead(downloadURL);
    var success = false
    try {
      val code = httpclient.execute(httpHead).getStatusLine.getStatusCode;
      if (code == 200 || code == 302 || code == 401 || code == 403) {
        success = true
      }

    } catch {
      case _: Throwable => success = false
    }
    success
  }

  def writeStats(statfile: String, success: Boolean, downloadURL: String) = {
    // save the stats
    //val timestamp: Long = System.currentTimeMillis / 1000
    val invocationTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())

    val stattsv = invocationTime + "\t" + success + "\t" + downloadURL + "\n"
    val stathtml = s"<tr><td>$invocationTime</td><td>$success</td><td>$downloadURL/td></tr>\n"
    writefile(statfile+".htmltable", stathtml, true)
    writefile(statfile+".tsv", stattsv, true)
  }

  def writefile(file: String, contents: String, append: Boolean) = {
    val fw = new FileWriter(file, append)
    try {
      fw.write(contents)
      println(s"written (append: $append) " + file)
    }
    finally fw.close()

  }

  def getSuccessRate(statfile: String) = {
    val bufferedSource = io.Source.fromFile(statfile)
    var count = 0f
    var successcount = 0f

    for (line <- bufferedSource.getLines) {
      val split = line.split("\t")
      // val cols = line.split("\t").map(_.trim)
      count += 1
      if (split(1).trim == "true") {
        successcount += 1
      }
      //	println(s"${cols(0)}|${cols(1)}|${cols(2)}")
    }
    bufferedSource.close
    successcount / count
  }


  def getRecursiveListOfFiles(dir: File): Array[File] = {
    val these = dir.listFiles
    these ++ these.filter(_.isDirectory).flatMap(getRecursiveListOfFiles)
  }

  //#4c1

  def writeSVG(svgfile: String, successrate: Float) = {

    var color = successrate match {
      case x if x >= 0.95f => "#4c1"
      case x if x < 0.95f => "#fc0"
      case _ => "#fc0"
    }
    val percentage = BigDecimal((successrate * 100).toDouble).setScale(2, BigDecimal.RoundingMode.HALF_UP) + "%"

    val svg =
      s"""<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:cc="http://creativecommons.org/ns#"
   xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
   xmlns:svg="http://www.w3.org/2000/svg"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:sodipodi="http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd"
   xmlns:inkscape="http://www.inkscape.org/namespaces/inkscape"
   width="90"
   height="20"
   id="svg2"
   version="1.1"
   inkscape:version="0.91 r13725"
   sodipodi:docname="test.svg">
  <metadata
     id="metadata31">
    <rdf:RDF>
      <cc:Work
         rdf:about="">
        <dc:format>image/svg+xml</dc:format>
        <dc:type
           rdf:resource="http://purl.org/dc/dcmitype/StillImage" />
      </cc:Work>
    </rdf:RDF>
  </metadata>
  <defs
     id="defs29" />
  <sodipodi:namedview
     pagecolor="#ffffff"
     bordercolor="#666666"
     borderopacity="1"
     objecttolerance="10"
     gridtolerance="10"
     guidetolerance="10"
     inkscape:pageopacity="0"
     inkscape:pageshadow="2"
     inkscape:window-width="1920"
     inkscape:window-height="1031"
     id="namedview27"
     showgrid="false"
     inkscape:zoom="7.8888889"
     inkscape:cx="45"
     inkscape:cy="10"
     inkscape:window-x="0"
     inkscape:window-y="27"
     inkscape:window-maximized="1"
     inkscape:current-layer="g17" />
  <linearGradient
     id="a"
     x2="0"
     y2="100%">
    <stop
       offset="0"
       stop-color="#bbb"
       stop-opacity=".1"
       id="stop5" />
    <stop
       offset="1"
       stop-opacity=".1"
       id="stop7" />
  </linearGradient>
  <rect
     rx="3"
     width="90"
     height="20"
     fill="#555"
     id="rect9" />
  <rect
     rx="3"
     x="37"
     width="53"
     height="20"
     fill="$color"
     id="rect11" />
  <path
     fill="#4c1"
     d="M37 0h4v20h-4z"
     id="path13" />
  <rect
     rx="3"
     width="90"
     height="20"
     fill="url(#a)"
     id="rect15" />
  <g
     fill="#fff"
     text-anchor="middle"
     font-family="DejaVu Sans,Verdana,Geneva,sans-serif"
     font-size="11"
     id="g17">
    <text
       x="19.5"
       y="15"
       id="text19"
       style="fill:#010101;fill-opacity:0.3">online</text>
    <path
       d="M 17.464844 5.6425781 L 17.464844 14 L 18.453125 14 L 18.453125 5.6425781 L 17.464844 5.6425781 z M 20.515625 5.6425781 L 20.515625 6.8945312 L 21.503906 6.8945312 L 21.503906 5.6425781 L 20.515625 5.6425781 z M 6.0878906 7.8398438 C 5.2249349 7.8398438 4.5491536 8.1191406 4.0585938 8.6777344 C 3.5716146 9.2363281 3.328125 10.009766 3.328125 10.998047 C 3.328125 11.982747 3.5716146 12.756185 4.0585938 13.318359 C 4.5491536 13.876953 5.2249349 14.15625 6.0878906 14.15625 C 6.9472656 14.15625 7.6227214 13.876953 8.1132812 13.318359 C 8.6038411 12.756185 8.8496094 11.982747 8.8496094 10.998047 C 8.8496094 10.009766 8.6038411 9.2363281 8.1132812 8.6777344 C 7.6227214 8.1191406 6.9472656 7.8398438 6.0878906 7.8398438 z M 13.382812 7.8398438 C 12.963867 7.8398437 12.591797 7.9283854 12.269531 8.1074219 C 11.950846 8.2864583 11.673828 8.5563151 11.4375 8.9179688 L 11.4375 7.984375 L 10.445312 7.984375 L 10.445312 14 L 11.4375 14 L 11.4375 10.599609 C 11.4375 10.01237 11.592448 9.5491536 11.900391 9.2089844 C 12.208333 8.8688151 12.628581 8.6992188 13.162109 8.6992188 C 13.60612 8.6992188 13.938151 8.8401693 14.160156 9.1230469 C 14.382161 9.4059245 14.494141 9.8330078 14.494141 10.402344 L 14.494141 14 L 15.482422 14 L 15.482422 10.369141 C 15.482422 9.5384115 15.305664 8.9104818 14.951172 8.484375 C 14.59668 8.0546875 14.073893 7.8398438 13.382812 7.8398438 z M 26.466797 7.8398438 C 26.047852 7.8398437 25.677734 7.9283854 25.355469 8.1074219 C 25.036784 8.2864583 24.757812 8.5563151 24.521484 8.9179688 L 24.521484 7.984375 L 23.527344 7.984375 L 23.527344 14 L 24.521484 14 L 24.521484 10.599609 C 24.521484 10.01237 24.676432 9.5491536 24.984375 9.2089844 C 25.292318 8.8688151 25.712565 8.6992188 26.246094 8.6992188 C 26.690104 8.6992188 27.024089 8.8401693 27.246094 9.1230469 C 27.468099 9.4059245 27.578125 9.8330078 27.578125 10.402344 L 27.578125 14 L 28.566406 14 L 28.566406 10.369141 C 28.566406 9.5384115 28.389648 8.9104818 28.035156 8.484375 C 27.680664 8.0546875 27.157878 7.8398438 26.466797 7.8398438 z M 33.066406 7.8398438 C 32.164063 7.8398438 31.447591 8.1289062 30.914062 8.7089844 C 30.384115 9.2854818 30.119141 10.066081 30.119141 11.050781 C 30.119141 12.003255 30.398438 12.759766 30.957031 13.318359 C 31.519206 13.876953 32.278646 14.15625 33.238281 14.15625 C 33.621419 14.15625 34.000651 14.115885 34.373047 14.037109 C 34.745443 13.958333 35.108398 13.84375 35.462891 13.693359 L 35.462891 12.759766 C 35.111979 12.945964 34.75651 13.084635 34.398438 13.177734 C 34.043945 13.270833 33.678385 13.318359 33.298828 13.318359 C 32.643555 13.318359 32.130534 13.139648 31.761719 12.785156 C 31.396484 12.427083 31.193359 11.908854 31.150391 11.228516 L 35.693359 11.228516 L 35.693359 10.746094 C 35.693359 9.8509115 35.457031 9.1422526 34.984375 8.6230469 C 34.515299 8.1002604 33.875651 7.8398438 33.066406 7.8398438 z M 20.515625 7.984375 L 20.515625 14 L 21.503906 14 L 21.503906 7.984375 L 20.515625 7.984375 z M 6.0878906 8.6777344 C 6.6142578 8.6777344 7.031901 8.8857422 7.3398438 9.3046875 C 7.6477865 9.7200521 7.8007812 10.285482 7.8007812 10.998047 C 7.8007813 11.714193 7.6477865 12.279948 7.3398438 12.695312 C 7.031901 13.110677 6.6142578 13.318359 6.0878906 13.318359 C 5.554362 13.318359 5.1341146 13.111003 4.8261719 12.699219 C 4.5218099 12.283854 4.3691406 11.717773 4.3691406 10.998047 C 4.3691406 10.27832 4.5240885 9.7125651 4.8320312 9.3007812 C 5.139974 8.8854167 5.5579427 8.6777344 6.0878906 8.6777344 z M 33.078125 8.6777344 C 33.565104 8.6777344 33.95638 8.8378906 34.25 9.1601562 C 34.547201 9.4824219 34.697917 9.914388 34.705078 10.455078 L 31.181641 10.460938 C 31.231771 9.8951823 31.422526 9.4560547 31.751953 9.1445312 C 32.084961 8.8330078 32.526693 8.6777344 33.078125 8.6777344 z "
       id="text21" />
    <text
       x="62.5"
       y="15"
       id="text23"
       style="fill:#010101;fill-opacity:0.3" />
    <text
       x="62.5"
       y="14"
       id="text25">$percentage</text>
  </g>
</svg>
"""
    writefile(svgfile, svg, false)

  }
}
