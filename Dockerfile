# Используем базовый образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Установка SBT
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
EXPOSE 9001
CMD ["java", "-jar", "target/scala-3.5.1/ArtOfJoy.jar" ]