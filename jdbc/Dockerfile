FROM ubuntu:latest AS builder

ENV GRAALVM_VERSION 21.2.0

RUN apt-get update
RUN apt-get install -y binutils
RUN apt-get install -y binutils-common
RUN apt-get install -y build-essential
RUN apt-get install -y g++
RUN apt-get install -y gcc
RUN apt-get install -y libc6-dev
RUN apt-get install -y make
RUN apt-get install -y tar
RUN apt-get install -y wget
RUN apt-get install -y zlib1g-dev

RUN mkdir -p /usr/lib/jvm \
  && cd /usr/lib/jvm \
  && wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAALVM_VERSION}/graalvm-ce-java11-linux-amd64-${GRAALVM_VERSION}.tar.gz \
  && tar xzvf graalvm-ce-java11-linux-amd64-${GRAALVM_VERSION}.tar.gz \
  && ln -s graalvm-ce-java11-${GRAALVM_VERSION} graalvm \
  && ./graalvm/bin/gu install native-image

RUN mkdir -p /build
COPY .mvn /build/.mvn
COPY config /build/config
COPY pom.xml mvnw /build/
COPY src /build/src

RUN cd /build \
  && export PATH=/lib/bin:$PATH \
  && export JAVA_HOME=/usr/lib/jvm/graalvm \
  && export GRAALVM_HOME=/usr/lib/jvm/graalvm \
  && ./mvnw install -Dmaven.test.skip=true -Pnative

FROM alpine:3.14

RUN apk update \
  && apk add --no-cache gcompat

COPY --from=builder /build/target/*-runner /usr/local/bin/application

EXPOSE 8080

CMD ["application"]
