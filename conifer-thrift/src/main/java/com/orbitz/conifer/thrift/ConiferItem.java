package com.orbitz.conifer.thrift;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct
public final class ConiferItem {

    private final String key;
    private final ContentType contentType;
    private final byte[] content;

    @ThriftConstructor
    public ConiferItem(String key, ContentType contentType, byte[] content) {
        this.key = key;
        this.contentType = contentType;
        this.content = content;
    }

    @ThriftField(1)
    public String getKey() {
        return key;
    }

    @ThriftField(2)
    public ContentType getContentType() {
        return contentType;
    }

    @ThriftField(3)
    public byte[] getContent() {
        return content;
    }
}
