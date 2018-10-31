package com.datahack.akka.introduction.actors

import akka.actor.{Actor, ActorLogging}
import com.datahack.akka.introduction.actors.Teacher.{Advice, AskAdvice, IDoNotUnderstand}

object Teacher {  // Companion object con mensajes

  case class AskAdvice(topic: String)
  case class Advice(text: String)
  case object IDoNotUnderstand

}

class Teacher extends Actor with ActorLogging {

  log.debug(s"${self.path} actor created")

  val advices: Map[String, String] = Map[String, String](
    "History" -> "History Advice",
    "Math" -> "Math Advice",
    "Geography" -> "Geography Advice"
  )

  override def receive: Receive = {

    case AskAdvice(topic) =>
      val response = advices.get(topic).map(advice => Advice(advice)).getOrElse(IDoNotUnderstand)
      log.info(response.toString)
      sender ! response
  }
}
