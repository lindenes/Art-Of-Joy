ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.0"

lazy val zioConfig = "4.0.1"
lazy val zio = "2.0.21"
lazy val zioLib = Seq(
  "dev.zio" %% "zio"                 % zio,
  "dev.zio" %% "zio-streams"         % zio,
  "dev.zio" %% "zio-config"          % zioConfig,
  "dev.zio" %% "zio-config-magnolia" % zioConfig,
  "dev.zio" %% "zio-config-typesafe" % zioConfig,
  "dev.zio" %% "zio-http"            % "3.0.0-RC4",
  "dev.zio" %% "zio-json"            % "0.6.2"
)

lazy val root = (project in file("."))
  .settings(
    name := "Art-Of-Joy",
    libraryDependencies ++= Seq(
      "io.getquill"          %% "quill-jdbc-zio" % "4.8.1",
      "org.postgresql"       %  "postgresql"     % "42.7.1",
      "org.slf4j"            % "slf4j-api"       % "2.0.9",
      "ch.qos.logback"       % "logback-classic" % "1.4.14",
    ) ++ zioLib
  )
