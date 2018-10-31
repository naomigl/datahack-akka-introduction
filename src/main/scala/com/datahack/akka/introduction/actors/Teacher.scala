package com.datahack.akka.introduction.actors

import akka.actor.Actor
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}

object Teacher {  // Companion object con mensajes

  case class AskAdvice(topic: String)
  case class Advice(text: String)
  case object IDoNotUnderstand

}

class Teacher extends Actor {

  val advices: Map[String, String] = Map[String, String](
    "History" -> "History Advice",
    "Math" -> "Math Advice",
    "Geography" -> "Geography Advice"
  )

  override def receive: Receive = {

    case AskAdvice(topic) =>
      val response = advices.get(topic).map(advice => Advice(advice)).getOrElse(IDoNotUnderstand)
      println(response)
      sender ! response
  }
}
