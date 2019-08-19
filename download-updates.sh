#!/bin/sh
QUERY="PREFIX dataid: <http://dataid.dbpedia.org/ns/core#>
PREFIX dcat:   <http://www.w3.org/ns/dcat#>

SELECT ?file ?sha256sum ?downloadURL   WHERE {
  ?s dcat:downloadURL ?downloadURL . 
  ?s dataid:sha256sum ?sha256sum .
  ?s dataid:file ?file .
} "

curl -d "format=text%2Ftab-separated-values" \
--data-urlencode "query=$QUERY" \
"https://databus.dbpedia.org/repo/sparql" > /tmp/online-updates.tsv
