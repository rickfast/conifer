package com.orbitz.conifer;

import com.orbitz.conifer.discover.Discovery;
import com.orbitz.conifer.serde.SerDe;
import com.orbitz.conifer.thrift.ConiferItem;

public class Client<T> {
    private Discovery discovery;
    private String cache;
    private SerDe serDe;
    private Class<T> type;

    Client(String cache, Discovery discovery, SerDe serDe, Class<T> type) {
        this.cache = cache;
        this.discovery = discovery;
        this.serDe = serDe;
        this.type = type;
    }

    public void put(String key, T data) {
        discovery.discover().put(cache, new ConiferItem(key, serDe.contentType(), serDe.serialize(data)));
    }

    public T get(String key) {
        ConiferItem item = discovery.discover().get(cache, key);

        return serDe.deserialize(item.getContent(), type);
    }
}
