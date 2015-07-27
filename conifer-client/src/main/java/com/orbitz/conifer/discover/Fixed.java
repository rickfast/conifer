package com.orbitz.conifer.discover;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import com.google.common.net.HostAndPort;
import com.orbitz.conifer.thrift.ConiferCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static com.google.common.net.HostAndPort.fromParts;

public class Fixed implements Discovery {

    private List<ConiferCache> cacheClients;
    private Random random = new Random();

    public Fixed(HostAndPort... hostAndPorts) {
        ThriftClientManager clientManager = new ThriftClientManager();

        cacheClients = new ArrayList<ConiferCache>();

        for(HostAndPort hostAndPort : hostAndPorts) {
            FramedClientConnector connector = new FramedClientConnector(fromParts(hostAndPort.getHostText(),
                    hostAndPort.getPort()));

            try {
                cacheClients.add(clientManager.createClient(connector, ConiferCache.class).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ConiferCache discover() {
        int index = random.nextInt(cacheClients.size());

        return cacheClients.get(index);
    }
}
