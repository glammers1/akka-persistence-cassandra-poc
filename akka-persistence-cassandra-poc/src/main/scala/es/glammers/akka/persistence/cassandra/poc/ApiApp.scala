package es.glammers.akka.persistence.cassandra.poc

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{Offset, PersistenceQuery}
import akka.stream.ActorMaterializer

object ApiApp {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem             = ActorSystem("ApiSystem")
    implicit val log: LoggingAdapter             = system.log
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val interface = "0.0.0.0"
    val port      = 8888

    // actor configuration
    val counterRef: ActorRef = system.actorOf(Props[CounterPersistentActor], "myActor")

    val cassandraReadJournal: CassandraReadJournal = PersistenceQuery(system)
      .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
    val offset: Offset = cassandraReadJournal.timeBasedUUIDFrom(0L)
    cassandraReadJournal
      .eventsByTag("myTag", offset)
      .runForeach(event =>
        log.info(
          s"pid: ${event.persistenceId}, sequenceNr: ${event.sequenceNr}, offset: ${event.offset}, event: ${event.event}"))

    // Bind service
    Http().bindAndHandle(routes, interface, port)
    log.info(s"Listening on $interface:$port")

    def routes: Route = {
      import akka.http.scaladsl.server.Directives._

      pathPrefix("counters" / "increment") {
        pathEndOrSingleSlash {
          post {
            complete {
              counterRef ! Cmd(1)
              "incremented"
            }
          }
        }
      } ~ pathPrefix("state") {
        pathEndOrSingleSlash {
          get {
            complete {
              counterRef ! "print"
              "done"
            }
          }
        }
      }
    }
  }
}
