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

  def getTopic: Gen[String] = Gen.oneOf("History", "Maths", "Geography", "Physics", "Literature")

  override def receive: Receive = {

    case PerformAnAdviceRequest =>
      val topic = getTopic.sample.get
      teacher ! AskAdvice(topic)

    case Advice(text) =>
      log.info(s"The requested advice: $text.")

    case IDoNotUnderstand =>
      log.warning("Ups I do not know what happens.")

  }
}
