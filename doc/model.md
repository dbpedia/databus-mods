# Metadata Model

## Mod Activity

The central concept of the Databus Mods Ontology is the class mod:DatabusMod,
which is a subclass of prov:Activity. Instances of this class define Mod Activities
generating metadata using Databus files. Mods implementing this model should create a
sub-class of mod:DatabusMod to describe the performing Mod Activity further. Instances
of mod:DatabusMod are described with the following properties:

**mod:version** – Describes the version of the implementation performing the Mod
Activity, e.g, the semantic version of the form major.minor.patch or a Git3
commit hash.

**mod:statSummary** – The main summary of the Mod Activity like an overall rating.
The intend is to create a sub-property using rdfs:subPropertyOf describing the
the properties value by adding an rdfs:comment. This property is optional.

**prov:startedAtTime** – Is the date time when a Mod Activity is deemed to have
been started. The literal value is of the type xsd:dateTime.

**prov:endedAtTim**e – The time when a Databus Mod Activity ended. The literal
value is of the type xsd:dateTime.

**prov:used** – The prov:Entity that was used by the Databus Mod. The value of
this attribute is the same as specified by the property dataid:file in the DataID
and represents a stable Databus file identifier.

**prov:generated** – The result files generated by the activity. The values of this
attribute have the domain prov:Entity. In the case of Databus Mods the provided resource must be either of the sub type mod:Summary, mod:Statistics, or
mod:Enrichment

### Mod Activity Metadata

```turtle
@prefix mod: <http://dataid.dbpedia.org/ns/mod#> .
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix n0: <https://databus.dbpedia.org/vehnem/paper-supplements/> .

<activity.ttl> a mod:OnlineCheckMod ;
  mod:version        "1.0.0" ;
  prov:used          n0:demo-graph\/20210301\/demo-graph.png ;
  prov:startedAtTime "2021-04-15T12:05:36.384Z"^^xsd:#dateTime ;
  prov:endedAtTime   "2021-04-15T12:05:36.851Z"^^xsd:dateTime ;
  mod:onlineRate     "100%"^^xsd:string ;
  prov:generated     <online.csv> ;
  prov:generated     <online.svg> .

<online.svg> mod:svgDerivedFrom n0:demo-graph\/20210301\/demo-graph.png .
<online.csv> mod:csvDerivedFrom n0:demo-graph\/20210301\/demo-graph.png .
```

## Mod Result

```turtle
@prefix dcat: <http://www.w3.org/ns/dcat#> .

g0:demo-graph\/20210301\/demo-graph.ttl
dcat:mediaType <http://dataid.dbpedia.org/ns/iana#text/turtle> .
dcat:compression <ttp://dataid.dbpedia.org/ns/mt#gz> .
```
