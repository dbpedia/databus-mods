# Mod Worker API

## Basic API Profile

`/{publisher}/{group}/{artifact}/{version}/{file}/activity`

**Methods:**

- POST

**URL parameters:**

- `{publisher}/{group}/{artifact}/{version}/{file}/activity`: path segments of the stable Databus file identifier. Data Parameters (in request body):

- `source`: required parameter containing a data access URI. For example the value of this parameter can be the dcat:downloadURL associated with the Databus file in the published DataID, or a cached file in a shared local storage between Master and Workers.

**Success Response:**

- `Code 200-OK`: The Mod Worker performed the Mod Activity successfully. The responses payload contains Mod Activity Metadata represented in RDF describing the performed Mod Activity with used file, process duration, Mod type, Worker version and generated Mod Results.

**Error Response:**

- `Code 400-Bad-Request`: The send request parameters are invalid and the Worker could not perform the Mod Activity.

- `Code 500-Internal-Server-Error`: The Mod Worker failed to perform its provided Mod Activity and produced no (meta)data.

## Pollingbased API Profile

## Backlog
- configuration of boot starter
- documentation: server setup, boot starter, re-implementation of APIs
- push metadata over master
- refactor ActivityController to be a blank trait
- create VocabFactory to help extend the existing Mod Vocab

### Components

- core: core components for master and workers
    - model
        - rdf generation
        - validation
    - worker client
    -

## Springboot Starter

### Http Api Documentation

---

#### Execute Mod Activity at Worker

<details>
 <summary><code>POST</code> <code><b>/activity</b></code> <code>(execute activity at worker)</code></summary>

##### Parameters

> | name   |  type     | data type | description          |
> |--------| -------|-----------|-----------|
> | dataId |  required | string    | IRI of dataid entity | 
> | accessIRI      |  optional | string    | access IRI of data | 

##### Responses

> | http code | content-type       | response                                  |
> |--------------------|-------------------------------------------|-----------------------------------|
> | `200`     | `text/turtle`      | `Descriptive Mod Activity Metadata`       |
> | `202`     | `text/plain`       | `Descriptive Mod Activity Metadata`       |
> | `400`     | `application/json` | `{"code":"400","message":"Bad Request"}`  |
> | `500`     | `application/json` | `{"code":"500","message":"${Exception}"}` |

##### Example cURL

> ```javascript
>  curl -X POST --data-urlencode "dataId=TODO" http://localhost:8080/
> ```

</details>

---

#### Polling Activity Status

<details>
 <summary><code>GET </code> <code><b>/${jobid}/activity</b></code> <code>(check activity at worker)</code></summary>

##### Responses

> | http code | content-type              | response                                                        |
> |---------------------------|-----------------------------------------------------------------|-----------------------------------|
> | `202`     | `text/turtle`             | `Configuration created successfully`                            |
> | `202`     | `text/plain`              | `Configuration created successfully`                            |
> | `400`     | `application/json`        | `{"code":"400","message":"Bad Request"}`                        |
> | `500`     |`application/json` | `{"code":"500","message":"${Exception}"}`                       |

##### Example cURL

> ```javascript
>  curl -X POST -H "Content-Type: application/json" --data @post.json http://localhost:8889/
> ```

</details>

---

## HTTP API Re-Implementation

[//]: # (The master will follow redirects &#40;includes 202&#41; and obeys retry after headers)

Requirements:
- Post request that ends with /activity

Pitfalls/Features:
- Activity Deduplication
- Resource Management: Delete local files after they have been accessed once
- 

