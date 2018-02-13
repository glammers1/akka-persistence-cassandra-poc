package es.glammers.akka.persistence.cassandra.poc

import akka.actor.Status.Failure
import akka.actor.{ActorLogging, Props}
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, Offset, PersistenceQuery}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

class ProjectionActor extends PersistentActor with ActorLogging {
  import ProjectionActor._

  override def persistenceId = "projection-pid"

  var state = ProjectionActorState(None)

  def initStream(): Unit = {
    implicit val mat: ActorMaterializer = ActorMaterializer()
    val cassandraReadJournal: CassandraReadJournal = PersistenceQuery(context.system)
      .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
    val offset: Offset = state.currentOffset.getOrElse(cassandraReadJournal.timeBasedUUIDFrom(0L))
    cassandraReadJournal
      .eventsByTag("myTag", offset)
      .runWith(Sink.actorRefWithAck(self, initMessage, ackMessage, completeMessage))
  }

  def updateState(event: OffsetChanged): Unit =
    state = state.updated(event)

  override def receiveRecover: Receive = {
    case evt: OffsetChanged => updateState(evt)
    case RecoveryCompleted  => initStream()
  }

  val receiveCommand: Receive = {
    case `initMessage` =>
      sender() ! ackMessage
      context.system.log.info("init message ProjectionActor")
    case event: EventEnvelope =>
      context.system.log.info(
        s"pid: ${event.persistenceId}, sequenceNr: ${event.sequenceNr}, offset: ${event.offset}, event: ${event.event}")
      persist(OffsetChanged(event.offset)) { evt: OffsetChanged =>
        updateState(evt)
      }
      sender() ! ackMessage
    case `completeMessage` =>
      context.system.log.error("Unexpected completeMessage")
      context.stop(self)
    case exceptionFromStream: Failure => // possible due to Sink.actorRef
      context.system.log.error(exceptionFromStream.cause, "Unexpected exceptionFromStream")
      context.stop(self)
  }
}

object ProjectionActor {
  def props = Props(new ProjectionActor)

  val initMessage     = "start"
  val completeMessage = "done"
  val ackMessage      = "ack"

  case class ProjectionActorState(currentOffset: Option[Offset]) {
    def updated(evt: OffsetChanged): ProjectionActorState = copy(Some(evt.offset))
  }

  case class OffsetChanged(offset: Offset)
}
