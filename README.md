
Exchange rates server
==
It's a simple Java application that can be used as an exchange rates server.
It can gather rates data from a set of sources (ECB, Yahoo Finance, etc.) and store rates in DB (PostgreSQL is used by default).
The stored rates data can be requested via REST API.

Compile and start
```mvn clean install exec:java
```

Get last rates from all sources
```/rest/rates/current
```

