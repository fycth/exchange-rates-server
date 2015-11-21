
Exchange rates server
=
It's a simple Java application that can be used as an exchange rates server.
It can gather rates data from a set of sources (ECB, Yahoo Finance, etc.) and store rates in DB (PostgreSQL is used by default).
The stored rates data can be requested via REST API.

Check code styling (checkstyle): ```mvn checkstyle:check```

Do static code analyzis (CPD/PMD): ```mvn site``` - results will be available at ```target/site/project-reports.html```

Compile and start: ```mvn clean install exec:java```

Rates REST API
-

Get last rates from all sources: ```/rest/rates/current```

Get rates for the certain source: ```/rest/rates/source/1```

Get rates for the certain group: ```/rest/rates/group/2```

Get rates for the default group: ```/rest/rates/group```

Put exchange rate data manually: ```/rest/rates/put/USD/1.5```

Get rates for certain time limit:
```
    /rest/rates/current?from=20150101
    /rest/rates/group?from=201501012012
    /rest/rates/source/2?from=20150101&to=20151012
```
State REST API
-
The application provides state REST API that can be used for integrating with Zabbix.

Get status for all data sources: ```/admin/rest/state```

Get status for specific data source: ```/admin/rest/state/1```

Admin web UI
-
Basic admin web UI is accessible at: ```/admin```
Can be used for administering data sourcers, groups, parsers.

