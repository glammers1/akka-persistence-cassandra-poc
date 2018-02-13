package es.glammers.akka.persistence.cassandra.poc

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import es.glammers.akka.persistence.cassandra.poc.CounterPersistentActor.Cmd

object ApiApp {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem             = ActorSystem("ApiSystem")
    implicit val log: LoggingAdapter             = system.log
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val interface = "0.0.0.0"
    val port      = 8888

    // actor configuration
    val counterRef: ActorRef =
      system.actorOf(CounterPersistentActor.props("1"), "CounterPersistentActor1")

    system.actorOf(ProjectionActor.props, "ProjectionActor")

    // Bind service
    Http().bindAndHandle(routes, interface, port)
    log.info(s"Listening on $interface:$port")

    def routes: Route = {
      import akka.http.scaladsl.server.Directives._

      pathPrefix("counters" / "increment") {
        pathEndOrSingleSlash {
          post {
            complete {
              counterRef ! Cmd
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
