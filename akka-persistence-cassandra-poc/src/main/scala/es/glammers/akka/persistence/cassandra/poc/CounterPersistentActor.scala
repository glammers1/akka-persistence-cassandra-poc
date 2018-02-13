package es.glammers.akka.persistence.cassandra.poc

import java.time.Instant

import akka.actor.Props
import akka.persistence._
import akka.persistence.journal.Tagged

class CounterPersistentActor(name: String) extends PersistentActor {
  import CounterPersistentActor._

  override def persistenceId = s"myPid-$name"

  var state = CounterPersistentActorState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  val receiveRecover: Receive = {
    case evt: Evt                                                => updateState(evt)
    case SnapshotOffer(_, snapshot: CounterPersistentActorState) ⇒ state = snapshot
  }

  val receiveCommand: Receive = {
    case Cmd =>
      persistAll(
        List(Tagged(Evt(1 + state.counter), Set("all", "myTag")),
             Tagged(MessageProcessed, Set("all", "myTag")))) {
        case Tagged(evt @ Evt(_), _) =>
          updateState(evt)
        case Tagged(_, _) =>
//          context.system.log.info("message processed")
          saveSnapshot(state)
      }
    case "print" => context.system.log.info(s"${state.counter}")
  }
}

object CounterPersistentActor {
  def props(name: String): Props = Props(new CounterPersistentActor(name))

  case class CounterPersistentActorState(counter: Int = 0) {
    def updated(evt: Evt): CounterPersistentActorState = copy(evt.data)
  }

  case object Cmd
  case class Evt(data: Int)
  case class MessageProcessed(
      messageId: String = "id",
      timestamp: Long = Instant.now.getEpochSecond
  )
}
