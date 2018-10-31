package com.datahack.akka.introduction.actors

import akka.actor.{Actor, ActorRef}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}
import org.scalacheck.Gen

object Student {

  case object PerformAnAdviceRequest
}

class Student(teacher: ActorRef) extends Actor {

  val genTopic: Gen[String] = Gen.oneOf(
    "History", "Math", "Geography", "Personal Issues"
  )

  override def receive: Receive = {

    case PerformAnAdviceRequest =>
      val topic = genTopic.sample.get
      println(s"Teacher!! I need your advice for $topic")
      teacher ! AskAdvice(topic)

    case Advice(advice) =>
      println(s"Got it! I will $advice")

    case IDoNotUnderstand =>
      println("Sorry, I'll ask you again sometime")
  }
}
