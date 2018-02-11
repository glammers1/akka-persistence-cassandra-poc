package es.glammers.akka.persistence.cassandra.poc

import java.time.Instant

import akka.actor.Props
import akka.persistence._
import akka.persistence.journal.Tagged

class CounterPersistentActor extends PersistentActor {
  import CounterPersistentActor._

  override def persistenceId = "myPid"

  var state = CounterPersistentActorState()

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
        case Tagged(_, _)            => context.system.log.info("message processed")

      }
    case "print" => context.system.log.info(s"${state.counter}")
  }
}

object CounterPersistentActor {
  def props: Props = Props(new CounterPersistentActor)

  case class CounterPersistentActorState(counter: Int = 0) {
    def updated(evt: Evt): CounterPersistentActorState = copy(evt.data + counter)
  }

  case class Cmd(data: Int)
  case class Evt(data: Int)
  case class MessageProcessed(
      messageId: String = "id",
      timestamp: Long = Instant.now.getEpochSecond
  )
}
