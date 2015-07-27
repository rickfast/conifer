package com.orbitz.conifer.discover;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.orbitz.conifer.thrift.ConiferCache;
import com.orbitz.conifer.thrift.Node;
import com.orbitz.conifer.thrift.Nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.net.HostAndPort.fromParts;

public class Dynamic implements Discovery {

    private ConcurrentMap<String, ConiferCache> cachedClients;
    private AtomicReference<List<ConiferCache>> clients;
    private Nodes nodes;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Random random;

    public Dynamic(HostAndPort master) {
        final ThriftClientManager clientManager = new ThriftClientManager();
        final FramedClientConnector connector = new FramedClientConnector(fromParts(master.getHostText(),
                master.getPort()));

        try {
            nodes = clientManager.createClient(connector, Nodes.class).get();

            Futures.addCallback(nodes.availableNodes(), new DiscoveryCallback());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConiferCache discover() {
        return clients.get().get(random.nextInt(clients.get().size()));
    }

    private class DiscoveryCallback implements FutureCallback<List<Node>> {

        final ThriftClientManager clientManager = new ThriftClientManager();

        @Override
        public void onSuccess(List<Node> result) {
            List<ConiferCache> newClients = new ArrayList<ConiferCache>();

            for (Node node : result) {
                if(cachedClients.containsKey(node.getHost())) {
                    newClients.add(cachedClients.get(node.getHost()));
                } else {
                    try {
                        FramedClientConnector connector = new FramedClientConnector(fromParts(node.getHost(),
                                node.getPort()));
                        ConiferCache newClient = clientManager.createClient(connector, ConiferCache.class).get();

                        newClients.add(newClient);
                        cachedClients.put(node.getHost(), newClient);

                        clients.set(newClients);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
