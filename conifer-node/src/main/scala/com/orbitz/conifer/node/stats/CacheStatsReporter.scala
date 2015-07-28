package com.orbitz.conifer.node.stats

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import com.hazelcast.core.{IMap, HazelcastInstance}

import scala.concurrent.duration._
import scala.collection.JavaConversions._

class CacheStatsReporter @Inject() (system: ActorSystem,
                                    @Named("cacheStatsActor") statsActor: ActorRef) {

  def start() = {
    system.scheduler.schedule(0 milliseconds,
      1 seconds,
      system.actorOf(Props(classOf[CacheStatsActor], this)),
      "tick")
  }
}

class CacheStatsActor @Inject() (hazelcast: HazelcastInstance) extends Actor {
  override def receive: Actor.Receive = {
    case "tick" => {
      hazelcast.getDistributedObjects.toSeq
        .filter(_.isInstanceOf[IMap])
        .map(_.asInstanceOf[IMap])
        .filter(_.getName.startsWith("cache-"))
        .foreach(hazelcast.getMap(s"stats").put(hazelcast.getLocalEndpoint.getUuid,
          _.getLocalMapStats))
    }
  }
}