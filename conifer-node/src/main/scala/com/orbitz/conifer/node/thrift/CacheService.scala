package com.orbitz.conifer.node.thrift

import com.google.inject.Inject
import com.hazelcast.core.HazelcastInstance
import com.orbitz.conifer.thrift.{ConiferItem, ConiferCache}

class CacheService @Inject() (hazelcast: HazelcastInstance) extends ConiferCache {

  override def put(cache: String, item: ConiferItem): Unit = {
    hazelcast.getMap(cache).put(item.getKey, item)
  }

  override def get(cache: String, key: String): ConiferItem =
    hazelcast.getMap(cache).get(key)
}
