package com.orbitz.conifer.thrift;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

@ThriftService
public interface ConiferCache {

    @ThriftMethod("Put")
    void put(String cache, ConiferItem item);

    @ThriftMethod("Get")
    ConiferItem get(String cache, String key);
}
