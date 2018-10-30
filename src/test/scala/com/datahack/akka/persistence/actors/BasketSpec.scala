package com.datahack.akka.persistence.actors

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.testkit.{TestKit, TestProbe}
import com.datahack.akka.persistence.actors.Basket.{Cmd, Evt, Print, State}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.reflect.io.Path

class BasketSpec
  extends TestKit(ActorSystem("BasketSpec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  /*
   * Important: It is not possible to use TestActorRef for testing PersistentActor or any other
   * persistence provided classes (e.g. AtLeastOnceDelivery) due to its synchronous nature.
   * Persistence traits perform asynchronous tasks in background in order to handle internal persistence related events.
   * Therefore, when testing persistent actors you should rely on asynchronous messaging using TestKit.
   */

  val basketActor: ActorRef = system.actorOf(Props(classOf[Basket]), "BasketActor")

  "Basket Actor" should {

    "add an event to the state and preserve it after restart" in {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      basketActor ! Cmd("foo")
      sender.expectMsg(Evt("foo-0"))
      sender.expectMsg(Evt("foo-1"))

      basketActor ! PoisonPill

      val newBasketActor: ActorRef = system.actorOf(Props(classOf[Basket]), "NewBasketActor")

      newBasketActor ! Print
      sender.expectMsg(State("List(foo-0, foo-1)"))
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
    Path("target/example").deleteRecursively
  }
}
