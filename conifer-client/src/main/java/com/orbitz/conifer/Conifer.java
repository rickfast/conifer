package com.orbitz.conifer;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import com.google.common.collect.ImmutableList;
import com.orbitz.conifer.serde.JsonSerDe;
import com.orbitz.conifer.serde.SerDe;
import com.orbitz.conifer.thrift.ConiferCache;
import com.orbitz.conifer.thrift.ConiferItem;
import com.orbitz.conifer.thrift.ContentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.common.net.HostAndPort.fromParts;

public class Conifer {

    private static ConiferCache coniferCache;

    private static final Map<ContentType, SerDe> serDes;

    static {
        ThriftClientManager clientManager = new ThriftClientManager();
        FramedClientConnector connector = new FramedClientConnector(fromParts("localhost", 58109));

        try {
            coniferCache = clientManager.createClient(connector, ConiferCache.class).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        List<SerDe> serDeList = ImmutableList.<SerDe>of(new JsonSerDe());
        serDes = new HashMap<ContentType, SerDe>(serDeList.size());

        for(SerDe serDe : serDeList) {
            serDes.put(serDe.contentType(), serDe);
        }
    }

    public static <T> Client forCache(String cache, ContentType contentType, Class<T> type) {

        return new Client<T>(cache, coniferCache, serDes.get(contentType), type);
    }

    public static class Item {
        public String value;
    }

    public static void main(String... args) {
        Client<Item> client = Conifer.forCache("hi", ContentType.JSON, Item.class);
        Item item = new Item();
        item.value = "bye";

        client.put("hi", item);

        client.get("hi");
    }

    public static class Client<T> {
        private ConiferCache coniferCache;
        private String cache;
        private SerDe serDe;
        private Class<T> type;

        private Client(String cache, ConiferCache coniferCache, SerDe serDe, Class<T> type) {
            this.cache = cache;
            this.coniferCache = coniferCache;
            this.serDe = serDe;
            this.type = type;
        }

        public void put(String key, T data) {
            coniferCache.put(cache, new ConiferItem(key, serDe.contentType(), serDe.serialize(data)));
        }

        public T get(String key) {
            ConiferItem item = coniferCache.get(cache, key);

            return serDe.deserialize(item.getContent(), type);
        }
    }
}
