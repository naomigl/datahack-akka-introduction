package com.datahack.akka.introduction.actors

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TeacherSpec
  extends TestKit(ActorSystem("TeacherSpec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher](new Teacher())

  "Teacher actor" should {

    "send a response message to the sender of the message Ask Advice" in  {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      teacherActor ! AskAdvice("Maths")

      sender.expectMsg(5 seconds, Advice("Anything worth doing is worth overdoing"))
    }

    "send IDoNotUnderstand response message to the sender of the message Ask Advice " +
      "when the teacher do not know anything about the topic" in  {
      val sender = TestProbe()
      implicit val senderRef: ActorRef = sender.ref

      teacherActor ! AskAdvice("NoTopic")

      sender.expectMsgType[IDoNotUnderstand.type] (5 seconds)
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
