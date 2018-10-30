package com.datahack.akka.introduction.actors

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.AskAdvice
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class StudentSpec
  extends TestKit(ActorSystem(
    "StudentSpec",
    ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val teacherMock: TestProbe = TestProbe()
  val studentActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherMock.ref))
  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher]
  val studentWithTeacherActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherActor))

  "Student Actor" should {

    "send an AskAdvice message to a teacher actor when it receives teh PerformAnAdviceRequest message " in {
      studentActor ! PerformAnAdviceRequest
      teacherMock.expectMsgType[AskAdvice](5 seconds)
    }

    "receive an Advice message form teacher actor when it ask for it" in {
      EventFilter.info(pattern = "The requested advice*", occurrences = 1) intercept {
        studentWithTeacherActor !  PerformAnAdviceRequest
      }
    }

    "receive an IDoNotUnderstand message form teacher actor when it ask about a topic " +
      "when the teacher do not know anything about" in {
      teacherActor.underlyingActor.advices = Map.empty[String, String]

      EventFilter.warning(message = "Ups I do not know what happens.", occurrences = 1) intercept {
        studentWithTeacherActor !  PerformAnAdviceRequest
      }
    }
  }

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

}
