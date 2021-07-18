# quarkus-example

This project uses [Quarkus](https://quarkus.io), the Supersonic Subatomic Java Framework.

## Build

The application can be packaged using:

```shell script
./gradlew build
```

It produces a native executable `quarkus-example-0.0.1-runner` file in the `./build/` directory.

## Running the application

You need a running PostgreSQL instance before running the app.

It may be launched with the following command:

```shell script
docker run --name postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
```

You can then execute the native executable with: `./build/quarkus-example-0.0.1-runner`

## Explore

The following endpoints are available when the application is running:

| Endpoint                        | Description                                 |
| ------------------------------- | ------------------------------------------- |
| http://localhost:8080/q/metrics | [Prometheus](https://prometheus.io) metrics |
| http://localhost:8080/q/openapi | [OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.0.md) specifications |

## Docker image

A Docker image with the application can be created using:

```shell script
./gradlew image
```
