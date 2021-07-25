FROM quay.io/quarkus/quarkus-distroless-image:1.0

COPY build/*-runner /usr/local/bin/application

EXPOSE 8080
USER nonroot

CMD ["application"]
