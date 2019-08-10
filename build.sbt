lazy val commonSettings = Seq(
  organization := "com.github.lolgab",
  version := "0.0.1",
  scalaVersion := "2.11.12"
)

lazy val root = project.in(file("."))
  .aggregate(
    core,
    `execution-context`,
    `scalajs-timers`,
    `java-timer`,
    `h2o-server`,
    `examples`
  )

lazy val core = project
  .settings(
    name := "libuv-core",
    commonSettings
  )
  .enablePlugins(ScalaNativePlugin)

lazy val `execution-context` = project
  .settings(
    name := "libuv-execution-context",
    commonSettings
  )
  .dependsOn(core)
  .enablePlugins(ScalaNativePlugin)

lazy val `scalajs-timers` = project
  .settings(
    name := "libuv-scalajs-timers",
    commonSettings
  )
  .dependsOn(core)
  .enablePlugins(ScalaNativePlugin)

lazy val `java-timer` = project
  .settings(
    name := "libuv-java-timer",
    commonSettings
  )
  .dependsOn(core)
  .enablePlugins(ScalaNativePlugin)

lazy val `h2o-server` = project
  .settings(
    name := "libuv-h2o",
    commonSettings
  )
  .dependsOn(core)
  .enablePlugins(ScalaNativePlugin)

lazy val examples = project.in(file("examples"))
  .settings(
    name := "libuv-examples",
    commonSettings
  )
  .dependsOn(core, `execution-context`, `scalajs-timers`, `java-timer`/*, `h2o-server`*/)
  .enablePlugins(ScalaNativePlugin)
