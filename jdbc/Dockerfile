FROM alpine:3.14

RUN apk update \
  && apk add --no-cache libc6-compat \
  && apk add --no-cache libstdc++ \
  && apk add --no-cache gcompat

COPY target/*-runner /usr/local/bin/application

CMD ["application"]
