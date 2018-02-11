import sbt._

object Version {

  object Scala {
    final val ScalaVersion: String = "2.12.4"
  }

  object Scalafmt {
    final val ScalafmtVersion: String = "1.3.0"
  }

  object Akka {
    final val AkkaVersion: String                     = "2.5.9"
    final val AkkaHttpVersion: String                 = "10.0.11"
    final val AkkaPersistenceCassandraVersion: String = "0.80"
  }

  object JsonMarshalling {
    final val AkkaHttpJson4sVersion: String = "1.18.1"
    final val Json4sVersion: String         = "3.5.3"
  }

  object LoggingFrameworks {
    final val LogBackClassicVersion: String = "1.2.3"
  }

  object Typesafe {
    final val ConfigVersion: String = "1.3.1"
  }

  object Typelevel {
    final val CatsVersion: String = "1.0.1"
  }
}

object Library {
  import Version.Akka._
  import Version.JsonMarshalling._
  import Version.LoggingFrameworks._
  import Version.Typelevel._
  import Version.Typesafe._

  object Akka {
    final val Slf4j: ModuleID        = "com.typesafe.akka" %% "akka-slf4j"     % AkkaVersion % Runtime
    final val Actor: ModuleID        = "com.typesafe.akka" %% "akka-actor"     % AkkaVersion
    final val Stream: ModuleID       = "com.typesafe.akka" %% "akka-stream"    % AkkaVersion
    final val AkkaHttp: ModuleID     = "com.typesafe.akka" %% "akka-http"      % AkkaHttpVersion
    final val AkkaHttpCore: ModuleID = "com.typesafe.akka" %% "akka-http-core" % AkkaHttpVersion
    final val AkkaHttpSprayJson
      : ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
  }

  object Persistence {
    final val AkkaPersistence: ModuleID = "com.typesafe.akka" %% "akka-persistence" % AkkaVersion
    final val AkkaPersistenceQuery
      : ModuleID = "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion
    final val AkkaPersistenceCassandra
      : ModuleID = "com.typesafe.akka" %% "akka-persistence-cassandra" % AkkaPersistenceCassandraVersion
  }

  object JsonMarshalling {
    final val AkkaHttpJson4s
      : ModuleID                      = "de.heikoseeberger" %% "akka-http-json4s" % AkkaHttpJson4sVersion
    final val Json4sNative: ModuleID  = "org.json4s"        %% "json4s-native"    % Json4sVersion
    final val Json4sExt: ModuleID     = "org.json4s"        %% "json4s-ext"       % Json4sVersion
    final val Json4sJackson: ModuleID = "org.json4s"        %% "json4s-jackson"   % Json4sVersion
  }

  object LoggingFrameworks {
    final val LogBackClassic
      : ModuleID = "ch.qos.logback" % "logback-classic" % LogBackClassicVersion % Runtime
  }

  object Typesafe {
    final val Config: ModuleID = "com.typesafe" % "config" % ConfigVersion
  }

  object Typelevel {
    final val CatsCore: ModuleID = "org.typelevel" %% "cats-core" % CatsVersion
  }
}
