package com.orbitz.conifer.serde;

import com.google.gson.Gson;
import com.orbitz.conifer.thrift.ContentType;

public class JsonSerDe implements SerDe {

    private Gson gson = new Gson();

    @Override
    public byte[] serialize(Object data) {
        return gson.toJson(data).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return gson.fromJson(new String(data), clazz);
    }

    @Override
    public ContentType contentType() {
        return ContentType.JSON;
    }
}
