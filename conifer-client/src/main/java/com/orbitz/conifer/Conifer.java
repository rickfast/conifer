package com.orbitz.conifer;

import com.google.common.collect.ImmutableList;
import com.orbitz.conifer.discover.Discovery;
import com.orbitz.conifer.serde.JsonSerDe;
import com.orbitz.conifer.serde.SerDe;
import com.orbitz.conifer.thrift.ContentType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conifer {

    private final Map<ContentType, SerDe> serDes;
    private final Discovery discovery;

    public Conifer(Discovery discovery) {
        List<SerDe> serDeList = ImmutableList.<SerDe>of(new JsonSerDe());
        serDes = new HashMap<ContentType, SerDe>(serDeList.size());

        for (SerDe serDe : serDeList) {
            serDes.put(serDe.contentType(), serDe);
        }

        this.discovery = discovery;
    }

    public <T> Client forCache(String cache, ContentType contentType, Class<T> type) {

        return new Client<T>(cache, discovery, serDes.get(contentType), type);
    }
}
