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

