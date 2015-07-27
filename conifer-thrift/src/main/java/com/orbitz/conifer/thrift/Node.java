package com.orbitz.conifer.thrift;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct
public final class Node {

    private String host;
    private int port;

    @ThriftConstructor
    public Node(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @ThriftField(1)
    public String getHost() {
        return host;
    }

    @ThriftField(2)
    public int getPort() {
        return port;
    }
}
