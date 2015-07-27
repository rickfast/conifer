package com.orbitz.conifer.thrift;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@ThriftService
public interface Nodes {

    @ThriftMethod("AvailableNodes")
    ListenableFuture<List<Node>> availableNodes();
}
