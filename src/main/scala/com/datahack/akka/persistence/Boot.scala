package com.datahack.akka.persistence

import akka.actor.{ActorSystem, Props}
import com.datahack.akka.introduction.Boot.actorSystem
import com.datahack.akka.persistence.actors.Basket
import com.datahack.akka.persistence.actors.Basket.{Cmd, Print}

object Boot extends App {

  //create the actor system
  val system = ActorSystem("PersistenceSystem")

  val persistentActor = system.actorOf(Props(classOf[Basket]), "basket-persistent-actor")

  persistentActor ! Print
  persistentActor ! Cmd("foo")
  persistentActor ! Cmd("baz")
  persistentActor ! "boom"
  persistentActor ! Cmd("bar")
  persistentActor ! "snap"
  persistentActor ! Cmd("buzz")
  persistentActor ! Print


  // Ensure that the constructed ActorSystem is shut down when the JVM shuts down
  sys.addShutdownHook(actorSystem.terminate())

}
