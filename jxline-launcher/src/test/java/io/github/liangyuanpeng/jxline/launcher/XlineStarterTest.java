package io.github.liangyuanpeng.jxline.launcher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XlineStarterTest {

    @Test
    public void testStartXline() throws Exception {
        try (XlineCluster xline = Xline.builder().withClusterName(getClass().getSimpleName()).build()) {
            xline.start();
        }
    }

    @Test
    public void testStartXlineCluster() throws Exception {
        try (XlineCluster xline = Xline.builder()
                .withClusterName(getClass()
                        .getSimpleName())
                .withNodes(3)
                .build()) {
            xline.start();
            Assertions.assertEquals(3,xline.containers().size());
        }
    }

}
