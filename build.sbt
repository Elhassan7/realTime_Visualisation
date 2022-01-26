ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.11"

lazy val root = (project in file("."))
  .settings(
    name := "readStreamV2"
  )

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.2.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"

dependencyOverrides ++= {
  Seq(

    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
  )
}

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "7.14.0"
libraryDependencies += "org.elasticsearch" % "elasticsearch-hadoop" % "7.14.0"

