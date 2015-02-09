def commonSettings(projectName: String) = Seq(
  name := projectName,
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.5"
)

def canvasSettings(projectName: String) = {
  commonSettings(projectName) ++
  workbenchSettings ++
  Seq(
    persistLauncher := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.scalaz"   %% "scalaz-core"  % "7.1.0",
      "org.scala-js" %%% "scalajs-dom" % "0.7.0",
      "com.lihaoyi"  %%% "utest"       % "0.3.0" % "test"
    ),
    bootSnippet := "doodle.ScalaJSExample().main();",
    testFrameworks += new TestFramework("utest.runner.Framework")
    //refreshBrowsers <<= refreshBrowsers.triggeredBy(packageJS in Compile)
  )
}

lazy val core = project.in(file("core")).
  enablePlugins(ScalaJSPlugin).
  settings(canvasSettings("doodle-core") : _*)

lazy val canvas = project.in(file("canvas")).
  enablePlugins(ScalaJSPlugin).
  dependsOn(core).
  settings(canvasSettings("doodle-canvas") : _*)

lazy val java2d = project.in(file("java2d")).
  dependsOn(core).
  settings(commonSettings("doodle-java2d") : _*)
