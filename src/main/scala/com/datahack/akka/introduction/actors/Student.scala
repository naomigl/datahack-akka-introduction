package com.datahack.akka.introduction.actors

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import org.scalacheck.Gen

object Student {

  case object PerformAnAdviceRequest
}

class Student(teacher: ActorRef) extends Actor with ActorLogging {

  log.debug(s"${self.path} actor created")

  val genTopic: Gen[String] = Gen.oneOf(
    "History", "Math", "Geography", "Personal Issues"
  )

  override def receive: Receive = {

    case PerformAnAdviceRequest =>
      val topic = genTopic.sample.get
      log.info(s"Teacher!! I need your advice for $topic")
      teacher ! AskAdvice(topic)

    case Advice(advice) =>
      log.info(s"Got it! I will $advice")

    case IDoNotUnderstand =>
      log.info("Sorry, I'll ask you again sometime")
  }
}
