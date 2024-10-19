ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.5.1"

lazy val zioConfig = "4.0.2"
lazy val zio = "2.1.6"
lazy val mail = "1.6.2"
lazy val exel = "5.3.0"
lazy val tapir = "1.10.8"
lazy val zLog  = "2.3.0"

lazy val zioLib = Seq(
  "dev.zio" %% "zio"                 % zio,
  "dev.zio" %% "zio-streams"         % zio,
  "dev.zio" %% "zio-config"          % zioConfig,
  "dev.zio" %% "zio-config-magnolia" % zioConfig,
  "dev.zio" %% "zio-config-typesafe" % zioConfig,
  "dev.zio" %% "zio-http"            % "3.0.1",
  "dev.zio" %% "zio-json"            % "0.6.2",
  "dev.zio" %% "zio-logging"         % zLog
)

lazy val root = (project in file("."))
  .settings(
    assembly / mainClass := Some("art_of_joy/Main"),
     assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs@_*) =>
         (xs map {_.toLowerCase}) match {
           case "services" :: xs =>
             MergeStrategy.filterDistinctLines
           case _ => MergeStrategy.discard
         }
      case "application.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    },
    assembly / assemblyJarName := "ArtOfJoy.jar",
    name := "Art-Of-Joy",
    mainClass := Some("art_of_joy.Main"),
    Compile / mainClass := Some("art_of_joy.Main"),
    libraryDependencies ++= Seq(
      "io.getquill"          %% "quill-jdbc-zio" % "4.8.4",
      "org.postgresql"       %  "postgresql"     % "42.7.3",
      "org.slf4j"            % "slf4j-api"       % "2.0.12",
      "ch.qos.logback"       % "logback-classic" % "1.5.6",
      "javax.mail"           % "javax.mail-api"  %  mail,
      "com.sun.mail"         % "javax.mail"      %  mail,
      "org.apache.poi"       % "poi"             %  exel,
      "org.apache.poi"       % "poi-ooxml"       %  exel
    ) ++ zioLib
  )
