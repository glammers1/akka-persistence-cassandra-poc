import Library._
import sbt.Keys._

lazy val root = project
  .copy(id = "akka-persistence-cassandra")
  .in(file("."))
  .aggregate(
    `akka-persistence-cassandra-poc`
  )

lazy val `akka-persistence-cassandra-poc` =
  project
    .settings(
      libraryDependencies ++= Seq(
        Akka.Stream,
        Akka.Actor,
        Akka.AkkaHttp,
        Akka.AkkaHttpCore,
        Akka.Slf4j,
        Akka.AkkaHttpSprayJson,
        Persistence.AkkaPersistence,
        Persistence.AkkaPersistenceQuery,
        Persistence.AkkaPersistenceCassandra,
        JsonMarshalling.AkkaHttpJson4s,
        JsonMarshalling.Json4sNative,
        JsonMarshalling.Json4sExt,
        JsonMarshalling.Json4sJackson,
        LoggingFrameworks.LogBackClassic,
        Typesafe.Config,
        Typelevel.CatsCore
      )
    )
