# quarkus-jdbc

## Prerequisites

## Build

The application can be packaged to a binary using:

```shell script
./mvnw install -Pnative
```
The application can be packaged to a Docker image using:

```shell script
./mvnw install -Pdocker
```

## Extension

An extension can be added by building and deploying it first into local
repository using:

```shell script
cd ../greeting-extension
./mvnw cleen install
cd -
```
Then the extension can be added to the application by providing is in the command line:

```shell script
./mvnw install -Pnative -Dextensions=com.github.vontikov:greeting-extension:1.0.0-SNAPSHOT
```
