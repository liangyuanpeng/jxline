package io.github.liangyuanpeng.jxline.launcher;

import org.testcontainers.containers.Network;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class XlineClusterImpl implements XlineCluster{

    private final List<XlineContainer> containers;
    private final List<String> endpoints;

    private final String clusterName;

    public XlineClusterImpl(String image,
                            String clusterName,
                            String prefix,
                            int nodes,
                            boolean ssl,
                            Collection<String> additionalArgs,
                            Network network,
                            boolean shouldMountDataDirectory){

        this.clusterName = clusterName;

        this.endpoints = IntStream.range(0, nodes)
                .mapToObj(i -> (prefix == null ? "xline" : prefix + "xline") + i)
                .collect(toList());

        this.containers = endpoints.stream()
                .map(e -> new XlineContainer(image, e, endpoints)
                        .withClusterToken(this.clusterName)
                        .withSll(ssl)
                        .withAdditionalArgs(additionalArgs)
                        .withNetwork(network)
                        .withShouldMountDataDirectory(shouldMountDataDirectory))
                .collect(toList());
    }

    @Override
    public String clusterName() {
        return null;
    }

    @Override
    public List<URI> clientEndpoints() {
        return containers.stream().map(XlineContainer::clientEndpoint).collect(toList());
    }

    @Override
    public List<XlineContainer> containers() {
        return Collections.unmodifiableList(containers);
    }

    @Override
    public void start() {
        final CountDownLatch latch = new CountDownLatch(containers.size());
        final AtomicReference<Exception> failedToStart = new AtomicReference<>();

        for (XlineContainer container : containers) {
            new Thread(() -> {
                try {
                    container.start();
                } catch (Exception e) {
                    failedToStart.set(e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        try {
            latch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (failedToStart.get() != null) {
            throw new IllegalStateException("Cluster failed to start", failedToStart.get());
        }

    }

    @Override
    public void stop() {
        containers.forEach(c->c.stop());
    }

    @Override
    public void close() {
        containers.forEach(c->c.close());
    }

}
