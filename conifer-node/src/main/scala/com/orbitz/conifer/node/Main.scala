package com.orbitz.conifer.node

import com.facebook.swift.codec.guice.ThriftCodecModule
import com.facebook.swift.service.ThriftServer
import com.facebook.swift.service.guice.ThriftServerModule
import com.facebook.swift.service.guice.ThriftServiceExporter._
import com.google.common.collect.ImmutableMap
import com.google.inject._
import com.hazelcast.config.{Config, JoinConfig, NetworkConfig}
import com.hazelcast.core.{Hazelcast, HazelcastInstance}
import com.orbitz.conifer.node.thrift.CacheService
import com.typesafe.config.{Config => Configuration, ConfigFactory}
import io.airlift.configuration.{ConfigurationModule, ConfigurationFactory}

class ConiferModule extends Module {

  override def configure(binder: Binder): Unit = {

    binder.bind(classOf[CacheService]).in(Scopes.SINGLETON)

    thriftServerBinder(binder).exportThriftService(classOf[CacheService])
  }

  @Provides
  @Singleton
  def config(): Configuration = ConfigFactory.load()

  @Provides
  @Singleton
  def hazelcast(@Inject config: Configuration): HazelcastInstance = {
    val joinConfig = new JoinConfig()

    joinConfig.getTcpIpConfig.setEnabled(true)
    joinConfig.getTcpIpConfig.addMember(config.getString("join.host"))
    joinConfig.getMulticastConfig.setEnabled(false)

    Hazelcast.newHazelcastInstance(
      new Config().setNetworkConfig(
        new NetworkConfig().setJoin(joinConfig)))
  }
}

object Main {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector(
      new ConfigurationModule(new ConfigurationFactory(ImmutableMap.of())),
      new ThriftCodecModule(),
      new ThriftServerModule(),
      new ConiferModule())

    injector.getInstance(classOf[ThriftServer]).start()
  }
}
