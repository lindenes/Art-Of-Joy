ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.4.0"

lazy val zioConfig = "4.0.1"
lazy val zio = "2.0.21"
lazy val mail = "1.6.2"
lazy val exel = "5.2.5"
lazy val tapir = "1.10.8"
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
    assembly / mainClass := Some("Main"),
     assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case "application.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    },
    assembly / assemblyJarName := "ArtOfJoy.jar",
    name := "Art-Of-Joy",
    mainClass := Some("art_of_joy.Main"),
    Compile / mainClass := Some("art_of_joy.Main"),
    libraryDependencies ++= Seq(
      "io.getquill"          %% "quill-jdbc-zio" % "4.8.1",
      "org.postgresql"       %  "postgresql"     % "42.7.1",
      "org.slf4j"            % "slf4j-api"       % "2.0.9",
      "ch.qos.logback"       % "logback-classic" % "1.5.2",
      "javax.mail"           % "javax.mail-api"  %  mail,
      "com.sun.mail"         % "javax.mail"      %  mail,
      "org.apache.poi"       % "poi"             %  exel,
      "org.apache.poi"       % "poi-ooxml"       %  exel,
      "com.softwaremill.sttp.tapir" %% "tapir-zio" % tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % tapir
    ) ++ zioLib
  )
