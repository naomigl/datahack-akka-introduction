package com.datahack.akka.introduction

import akka.actor.{ActorSystem, Props}
import com.datahack.akka.introduction.actors.Student.PerformAnAdviceRequest
import com.datahack.akka.introduction.actors.{Student, Teacher}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

object Boot extends App {

  // Initialize the ActorSystem
  val actorSystem = ActorSystem("UniversityMessageSystem")

  // Whe need an execution context to perform the concurrent code
  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  // Construct the Teacher Actor Ref
  val teacherActorRef = actorSystem.actorOf(Props[Teacher], "teacher")

  // Construct the Student Actor Ref
  val studentActorRef = actorSystem.actorOf(Props(classOf[Student], teacherActorRef), "student")

  // Start asking advices
  actorSystem.scheduler.schedule(5 seconds, 15 seconds, studentActorRef, PerformAnAdviceRequest)

  // Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(actorSystem.terminate())
}
