# Prerequisites

- JDK 11
- Apache Maven 3.8.1 (to build)

# Usage

This application is pre-built. You can use it directly.

To run:

```java -jar target/log-searcher.jar```

This should get the server up and running

The REST API endpoint for searching a log file in the /var/log directory is

```localhost:8080/logs/filename```

For example, if you want to query the file with the name system.log, you can call (using a tool like curl). 

```localhost:8080/logs/system.log```

If such file does not exist in the /var/log directory, 404 will be returned

By default, at most 500 entries are returned, with the latest log events presented first. If you want to specify the max number of matching entries, you can use the query param maxNumOfEntries

```localhost:8080/logs/system.log?maxNumOfEntries=50```

If you want to search for logs that match certain keywords, you can use the query param keyword to match one or more keywords

```localhost:8080/logs/system.log?maxNumOfEntries=50&keyword=error&keyword=abnormal```

# Development

To build the project:

```mvn clean package```
