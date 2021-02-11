#!/usr/bin/env python3

import sys, requests, json
from SPARQLWrapper import SPARQLWrapper, JSON
import matplotlib.pyplot as plt
from wordcloud import WordCloud

if len(sys.argv) != 2:
    print("usage: "+sys.argv[0]+" <databusid>")
    exit(1)

databusid = "https://databus.dbpedia.org/"+sys.argv[1]

# get prefixes
prefix_cc = "http://prefix.cc/context"
context = json.loads(requests.get(prefix_cc).text)['@context']

prefixByFqn = {}
for prefix in context:
    fqn = context[prefix]
    if not fqn in prefixByFqn:
        prefixByFqn[fqn] = prefix

sparql = SPARQLWrapper("http://mods.tools.dbpedia.org/sparql")
sparql.setReturnFormat(JSON)

# get N = |D|
sparql.setQuery("""
PREFIX void: <http://rdfs.org/ns/void#>
PREFIX prov: <http://www.w3.org/ns/prov#>
SELECT (COUNT(DISTINCT ?s) AS ?c) WHERE {
  ?s prov:generated/void:classPartition ?p .
} 
""")

# N = int(sparql.query().convert()["results"]["bindings"][0]['c']['value'])
N = 9218
print("N = %s" % N)

# t in D und f(t) in D
sparql.setQuery("""
PREFIX void: <http://rdfs.org/ns/void#>
PREFIX prov: <http://www.w3.org/ns/prov#>
SELECT DISTINCT ?c ?t {
  ?s prov:used <%s> .
  ?s prov:generated/void:classPartition [
      void:class ?c ;
      void:triples ?t
  ]}
""" % databusid)

rs = sparql.query().convert()["results"]["bindings"]
nd = sum([ int(qs['t']['value']) for qs in rs])
print("n(d) = %s" % nd)

tf = {}
for qs in rs:
    term = qs['c']['value']
    frequency = int(qs['t']['value'])
    tf.update({term : frequency / nd})

tfIdf = {}
c = 0
for qs in rs:
    term = qs['c']['value']
    sparql.setQuery("""
    PREFIX void: <http://rdfs.org/ns/void#>
    PREFIX prov: <http://www.w3.org/ns/prov#>
    SELECT (COUNT(DISTINCT ?s) as ?c)  {
        ?s prov:generated/void:classPartition [
            void:class <%s> 
        ]
    }
    """ % term )
    df_t = int(sparql.query().convert()["results"]["bindings"][0]['c']['value'])
    idf_t = N/df_t
    # tf_idf.update({term: tf.get(term) * idf_t})
    prefixName = term
    a = term.rsplit('/', 1)
    if a[0]+"/" in prefixByFqn:
        prefixName = prefixByFqn.get(a[0]+"/")+":"+a[1]
    b = term.rsplit('#',1)
    if b[0]+"#" in prefixByFqn:
        prefixName = prefixByFqn.get(b[0]+"#")+":"+b[1]
    tfIdf_t = tf.get(term) * idf_t
    print("tfIdf[%s = %s] = %s" % (c,term,tfIdf_t) )
    tfIdf[prefixName] = tfIdf_t
    c += 1

wordcloud = WordCloud(background_color="white", width=1280, height=720)
wordcloud.generate_from_frequencies(frequencies=tfIdf)
plt.figure( figsize=(12.8,7.2))
plt.imshow(wordcloud, interpolation="bilinear")
plt.axis("off")
plt.show()


# top_10 = lambda :""
# top_10.values = [[u'nice', 2.0886619578149417],
#                  [u'owl', 2.2729656758128876],
#                  [u'person', 2.386294361119891],
#                  [u'read', 2.455287232606842],
#                  [u'seat', 2.5766480896111092]]
# d = {}
# for a, x in top_10.values:
#     d[a] = 3-x



