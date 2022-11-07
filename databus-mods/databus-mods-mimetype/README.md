# MimeType Mod

Tries to detect the true mime-type (media-type) of a file independent of the file extesion

Test
```bash
curl -Li localhost:9000/modApi --data-urlencode "databusID=https://databus.dbpedia.org/vehnem/paper-supplements/demo-graph/20210301/demo-graph.nt.gz" --data-urlencode "sourceID=https://databus.dbpedia.org/vehnem/paper-supplements/demo-graph/20210301/demo-graph.nt.gz"
```