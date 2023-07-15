package io.github.liangyuanpeng.jxline.launcher;

import org.testcontainers.containers.Network;
import org.testcontainers.shaded.com.google.common.base.Strings;

import java.util.*;

public class Xline {
    public static final String CONTAINER_IMAGE = "datenlord/xline:5068a2b";
    public static final int ETCD_CLIENT_PORT = 2379;

    private Xline() {
    }

    private static String resolveContainerImage() {
        String image = System.getenv("XLINE_IMAGE");
        if (!Strings.isNullOrEmpty(image)) {
            return image;
        }
        return CONTAINER_IMAGE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String image = Xline.resolveContainerImage();
        private String clusterName = UUID.randomUUID().toString();
        private String prefix;
        private int nodes = 1;
        private boolean ssl = false;
        private List<String> additionalArgs;
        private Network network;
        private boolean shouldMountDataDirectory = true;

        public Builder withClusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder withNodes(int nodes) {
            this.nodes = nodes;
            return this;
        }

        public Builder withSsl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }

        public Builder withAdditionalArgs(Collection<String> additionalArgs) {
            this.additionalArgs = Collections.unmodifiableList(new ArrayList<>(additionalArgs));
            return this;
        }

        public Builder withAdditionalArgs(String... additionalArgs) {
            this.additionalArgs = Collections.unmodifiableList(Arrays.asList(additionalArgs));
            return this;
        }

        public Builder withImage(String image) {
            this.image = image;
            return this;
        }

        public Builder withNetwork(Network network) {
            this.network = network;
            return this;
        }

        public XlineCluster build() {
            return new XlineClusterImpl(
                    image,
                    clusterName,
                    prefix,
                    nodes,
                    ssl,
                    additionalArgs,
                    network != null ? network : Network.newNetwork(),
                    shouldMountDataDirectory);
        }
    }

}
