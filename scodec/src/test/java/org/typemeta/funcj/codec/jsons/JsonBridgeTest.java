package org.typemeta.funcj.codec.jsons;

import org.junit.Test;

import java.io.*;

public class JsonBridgeTest {
    @Test
    public void test() throws Exception {
        final StringWriter wtr = new StringWriter();
        FileUtils.openResource("/example.json")
                .map(br -> new JsonBridge(br, wtr))
                .map(jb -> {
                    jb.run();
                    return 0;
                });

        wtr.close();
        //System.out.println(wtr);
    }
}
