import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport.{scalafmtOnCompile, scalafmtVersion}
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {

  override def requires = JvmPlugin

  override def trigger = allRequirements

  lazy val jvmSettings: Seq[String] = Seq("-Xms224M", "-Xmx224M", "-XX:+PrintCommandLineFlags")

  lazy val scalafmtSettings =
    Seq(
      scalafmtOnCompile in ThisBuild := true,
      scalafmtVersion in ThisBuild := Version.Scalafmt.ScalafmtVersion
    )

  override def projectSettings: Seq[Setting[_]] =
    Vector(
      organization in ThisBuild := "es.glammers",
      scalaVersion in ThisBuild := Version.Scala.ScalaVersion,
      crossScalaVersions in ThisBuild := Vector(scalaVersion.value),
      javacOptions in ThisBuild := Seq("-g:none"),
      javaOptions in ThisBuild ++= jvmSettings,
      fork in run in ThisBuild := true,
      fork in Test in ThisBuild := true,
      scalacOptions in ThisBuild ++= Vector(
        "-target:jvm-1.8",
        "-encoding",
        "utf-8",
        "-deprecation",
        "-explaintypes",
        "-feature",
        "-language:_",
        "-unchecked",
        "-Xcheckinit",
        "-Xfatal-warnings",
        "-Xfuture",
        "-Xlint",
        "-Yno-adapted-args",
        "-Ypartial-unification",
        "-Ywarn-dead-code",
        "-Ywarn-inaccessible",
        "-Ywarn-infer-any",
        "-Ywarn-nullary-override",
        "-Ywarn-nullary-unit",
        "-Ywarn-numeric-widen",
        "-Ywarn-unused",
        "-Ywarn-unused-import"
      ),
      updateOptions in ThisBuild := updateOptions.value.withCachedResolution(
        cachedResoluton = true),
      unmanagedSourceDirectories.in(Compile) in ThisBuild := Vector(scalaSource.in(Compile).value),
      unmanagedSourceDirectories.in(Test) in ThisBuild := Vector(scalaSource.in(Test).value)
    ) ++ scalafmtSettings
}
