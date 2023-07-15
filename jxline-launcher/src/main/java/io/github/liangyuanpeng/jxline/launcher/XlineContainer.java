package io.github.liangyuanpeng.jxline.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class XlineContainer extends GenericContainer<XlineContainer>{

    private static final Logger LOGGER = LoggerFactory.getLogger(XlineContainer.class);

    private final String node;
    private final Set<String> nodes;

    public XlineContainer(String image,String node,Collection<String> nodes){
        super(image);
        this.node=node;
        this.nodes = new HashSet<>(nodes);
        this.nodes.add(node);
    }

    @Override
    public void close(){
        super.close();
    }

    @Override
    protected void configure() {
        super.configure();
        withEnv("RUST_LOG","debug");
        withCommand(createCommand());
        String user = System.getenv("TC_USER");
        if (user != null) {
            withCreateContainerCmdModifier(c -> c.withUser(user));
        }
        withLogConsumer(new Slf4jLogConsumer(LOGGER).withPrefix(node));
        if (shouldMountDataDirectory) {
        }
        withNetworkAliases(node);
    }

    public String[] createCommand(){
        List<String> cmd = new ArrayList<>();
        cmd.add("xline");
        cmd.add("--name");
        cmd.add(node);
        cmd.add("--members");
        String members = node+"="+node+":2379";
        if (nodes.size() > 1) {
            members = "";
            for (String s : nodes) {
                members+=s+"="+s+":2379"+",";
            }
            members = members.substring(0,members.length()-1);
        }
        cmd.add(members);
        cmd.add("--data-dir");
        cmd.add("/tmp/xline");
        cmd.add("--storage-engine");
        cmd.add("rocksdb");
        return cmd.toArray(new String[0]);
    }

    private boolean ssl;

    private Collection<String> additionalArgs;
    private boolean shouldMountDataDirectory = true;
    private String clusterToken;

    public XlineContainer withSll(boolean ssl) {
        this.ssl = ssl;
        return self();
    }

    public XlineContainer withShouldMountDataDirectory(boolean shouldMountDataDiretory) {
        this.shouldMountDataDirectory = shouldMountDataDiretory;
        return self();
    }

    public XlineContainer withClusterToken(String clusterToken) {
        this.clusterToken = clusterToken;
        return self();
    }

    public XlineContainer withAdditionalArgs(Collection<String> additionalArgs) {
        if (additionalArgs != null) {
            this.additionalArgs = Collections.unmodifiableCollection(new ArrayList<>(additionalArgs));
        }
        return self();
    }

    public URI clientEndpoint() {
        return newURI(
                getHost(),
                getMappedPort(Xline.ETCD_CLIENT_PORT));
    }

    private URI newURI(final String host, final int port) {
        try {
            return new URI(ssl ? "https" : "http", null, host, port, null, null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URISyntaxException should never happen here", e);
        }
    }

}
