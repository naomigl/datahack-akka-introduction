package com.datahack.akka

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.actors.Teacher
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class TeacherSpec
  extends TestKit(ActorSystem("TeacherSpec"))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher](new Teacher())

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Teacher Actor" should {

    "send a response message to the sender of the message Ask Advice" in {
      val sender = TestProbe()
      implicit val senderRef = sender.ref

      teacherActor ! AskAdvice("Math")

      sender.expectMsg[Advice](5 seconds, Advice("Math Advice"))
    }

    "send IDoNotUnderstand when sender asks for Ask Advice not in list" in {
      val sender = TestProbe()
      implicit val senderRef = sender.ref

      teacherActor ! AskAdvice("NonExistingAdvice")

      sender.expectMsgType[IDoNotUnderstand.type](5 seconds)
    }
  }


}
