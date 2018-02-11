package es.glammers.akka.persistence.cassandra.poc

import java.time.Instant

import akka.persistence._
import akka.persistence.journal.Tagged

case class Cmd(data: Int)
case class Evt(data: Int)
case class MessageProcessed(messageId: String = "id", timestamp: Long = Instant.now.getEpochSecond)

case class ExampleState(counter: Int = 0) {
  def updated(evt: Evt): ExampleState = copy(evt.data + counter)
}

class CounterPersistentActor extends PersistentActor {
  import context._

  override def persistenceId = "myPid"

  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  val receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      persistAll(List(Tagged(Evt(data), Set("all", "myTag")),
                      Tagged(MessageProcessed, Set("all", "myTag")))) {
        case Tagged(evt @ Evt(_), _) => updateState(evt)
        case Tagged(_, _)            => system.log.info("message processed")

      }
    case "print" => system.log.info(s"${state.counter}")
  }
}
