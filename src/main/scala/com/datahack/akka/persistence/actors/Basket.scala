package com.datahack.akka.persistence.actors

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.datahack.akka.persistence.actors.Basket._

object Basket {
  // Messages
  case class Cmd(data: String)
  case class Evt(data: String)
  case object Print
  case object Snap
  case object Boom
  case class State(state: String)

  // Case class to store actor state
  case class BasketActorState(events: List[String] = Nil) {
    def updated(evt: Evt): BasketActorState = copy(evt.data :: events)
    def size: Int = events.length
    override def toString: String = events.reverse.toString
  }
}

class Basket extends PersistentActor with ActorLogging {

  //note : This is  mutable
  var state = BasketActorState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  override def persistenceId = "basket-persistent-actor"

  val receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_, snapshot: BasketActorState) => {
      log.info(s"offered state = $snapshot")
      state = snapshot
    }
  }

  val receiveCommand: Receive = {
    case Cmd(data) =>
      val event = Evt(s"$data-$numEvents")
      val newEvent = Evt(s"$data-${numEvents + 1}")
      persist(event)(updateState)
      persist(newEvent)(updateState)
      sender ! event
      sender ! newEvent
    case Snap  => saveSnapshot(state)
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"SaveSnapshotSuccess(metadata) :  metadata=$metadata")
    case SaveSnapshotFailure(metadata, reason) =>
      log.error("""SaveSnapshotFailure(metadata, reason) :metadata=$metadata, reason=$reason""")
    case Print =>
      println(state)
      sender ! State(state.toString)
    case Boom  => throw new Exception("boom")
  }

}
