package org.dbpedia.databus.dataid


class Part(
  val uri: String
) {
  var uriPath: String = _
  var downloadURL: String = _
  var byteSize: Long = _
  var sha256sum: String = _
  //  var compression: String = _
  //  var formatExtension: String = _
}

object Part {
  def apply(uri: String) : Part = {
    // TODO validate uri?
    val part = new Part(uri)
    part
  }
}

/*
         "@id": "http://localhost:3000/janni/generic/article-templates/2016.10.01#article-templates_nested_lang=am.ttl.bz2",
         "@type": "dataid:Part",
         "dataid:compression": "bzip2",
         "dataid:file": {
            "@id": "http://localhost:3000/janni/generic/article-templates/2016.10.01/article-templates_nested_lang=am.ttl.bz2"
         },
         "dataid:formatExtension": "ttl",
         "dataid:preview": "# started 2017-03-13T16:48:15Z\n<http://am.dbpedia.org/resource/ዋናው_ገጽ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Wikipediasister> .\n<http://am.dbpedia.org/resource/አዲስ_አበባ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Flag> .\n<http://am.dbpedia.org/resource/ኬንያ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:En> .\n<http://am.dbpedia.org/resource/ኬንያ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Sw> .\n<http://am.dbpedia.org/resource/ናይጄሪያ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:En> .\n<http://am.dbpedia.org/resource/ግብፅ> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Ar> .\n<http://am.dbpedia.org/resource/መለጠፊያ:በአፍሪካ_ውስጥ_የሚገኙ_አገሮች> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Navbox_subgroups> .\n<http://am.dbpedia.org/resource/መለጠፊያ:በአፍሪካ_ውስጥ_የሚገኙ_አገሮች> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Nowrap_begin> .\n<http://am.dbpedia.org/resource/መለጠፊያ:በአፍሪካ_ውስጥ_የሚገኙ_አገሮች> <http://am.dbpedia.org/property/wikiPageUsesTemplate> <http://am.dbpedia.org/resource/መለጠፊያ:Nowrap_end> .",
         "dataid:sha256sum": "8a87409181e197b4facc4b083b8ba12f39db2a126a0131c0f82b0750545c847c",
         "dataid-cv:lang": "am",
         "dataid-cv:tag": [
            "nested"
         ],
         "dct:conformsTo": "http://dataid.dbpedia.org/ns/core#",
         "dct:hasVersion": "2016.10.01",
         "dct:issued": {
            "@value": "2016-10-01T00:00:00Z",
            "@type": "xsd:dateTime"
         },
         "dct:license": {
            "@id": "http://purl.oclc.org/NET/rdflicense/cc-by3.0"
         },
         "dct:modified": {
            "@value": "2020-04-21T22:15:22Z",
            "@type": "xsd:dateTime"
         },
         "dcat:byteSize": {
            "@value": "5729",
            "@type": "xsd:decimal"
         },
         "dcat:downloadURL": {
            "@id": "https://downloads.dbpedia.org/repo/dbpedia/generic/article-templates/2016.10.01/article-templates_nested_lang=am.ttl.bz2"
         },
         "dcat:mediaType": {
            "@id": "dataid-mt:ApplicationNTriples"
         }
 */
