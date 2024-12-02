FROM docker.io/openjdk:17-jdk-slim AS build

RUN apt-get update && \
    apt-get install -y curl && \
    apt-get install -y gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && \
    apt-get install -y sbt
WORKDIR /usr/src/art-of-joy
COPY . .
RUN sbt compile
RUN sbt assembly

FROM docker.io/gcr.io/distroless/java17-debian12
COPY --from=build /usr/src/art-of-joy/target/scala-3.5.1/ArtOfJoy.jar ArtOfJoy.jar
CMD ["ArtOfJoy.jar" ]