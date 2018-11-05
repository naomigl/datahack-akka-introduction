package com.datahack.akka

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, TestActorRef, TestKit, TestProbe}
import com.datahack.akka.introduction.actors.{Student, Teacher}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class StudentSpec
  extends TestKit(
      ActorSystem("StudentSpec",
        ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")))
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val teacherRef: TestProbe = TestProbe()
  val studentActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherRef.ref))

  val teacherActor: TestActorRef[Teacher] = TestActorRef[Teacher](new Teacher())
  val studentWithRealActor: TestActorRef[Student] = TestActorRef[Student](new Student(teacherActor))

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Student Actor" should {

    "send an AskAdvice to the probe and be answered with an Advice" in {

      studentActor ! PerformAnAdviceRequest

      teacherRef.expectMsgType[AskAdvice](5 seconds)
    }

    "be responded with and Advice and print it" in {

      EventFilter.info("Got it! I will Math Advice", occurrences = 1) intercept {
        studentActor ! Advice("Math Advice")
      }
    }

    "be responded with IDoNotUnderstandAndPrintIt" in {

      EventFilter.info("Sorry, I'll ask you again sometime", occurrences = 1) intercept {
        studentActor ! IDoNotUnderstand
      }
    }
  }


}
