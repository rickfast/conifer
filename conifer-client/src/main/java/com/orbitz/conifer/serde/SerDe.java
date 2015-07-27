package com.orbitz.conifer.serde;

import com.orbitz.conifer.thrift.ContentType;

public interface SerDe {

    byte[] serialize(Object data);
    <T> T deserialize(byte[] data, Class<T> clazz);
    ContentType contentType();
}
