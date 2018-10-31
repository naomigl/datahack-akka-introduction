package com.datahack.akka.introduction

import akka.actor.{ActorSystem, Props}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.{Student, Teacher}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Boot extends App {

  val actorSystem = ActorSystem("UniversityMessageSystem")

  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  val teacherActorRef = actorSystem.actorOf(Props[Teacher], "teacher")
  val studentActorRef = actorSystem.actorOf(Props(classOf[Student], teacherActorRef), "student")

  actorSystem.scheduler.schedule(5 seconds, 15 seconds, studentActorRef, PerformAnAdviceRequest)

  sys.addShutdownHook(actorSystem.terminate())
}
