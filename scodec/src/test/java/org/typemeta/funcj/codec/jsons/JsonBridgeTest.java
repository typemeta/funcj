package org.typemeta.funcj.codec.jsons;

import org.junit.Test;

import java.io.PrintWriter;

public class JsonBridgeTest {
    @Test
    public void test() throws Exception {
        FileUtils.openResource("/example.json")
                .map(br -> {
                    new JsonBridge(br, new PrintWriter(System.out))
                            .run();
                    return 0;
                });
        System.out.println();
    }
}
