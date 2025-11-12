name := "LLMLibrary"

version := "0.1.0"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  // ZIO Core
  "dev.zio" %% "zio" % "2.0.19",
  "dev.zio" %% "zio-streams" % "2.0.19",

  // ZIO HTTP Client
  "dev.zio" %% "zio-http" % "3.0.0-RC4",

  // ZIO JSON for serialization
  "dev.zio" %% "zio-json" % "0.6.2",

  // Config loading
  "dev.zio" %% "zio-config" % "4.0.0-RC16",
  "dev.zio" %% "zio-config-typesafe" % "4.0.0-RC16",
  "dev.zio" %% "zio-config-magnolia" % "4.0.0-RC16",

  // Test dependencies
  "dev.zio" %% "zio-test" % "2.0.19" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.0.19" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// Scala compiler options
scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:higherKinds",
  "-language:implicitConversions"
)
