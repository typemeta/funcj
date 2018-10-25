package org.typemeta.funcj.codec.json;

import org.junit.Test;

import java.io.*;

public class JsonBridgeTest {
    @Test
    public void test() throws Exception {
        final StringWriter wtr = new StringWriter();
        final BufferedReader br = FileUtils.openResource("/example.json").getOrThrow();
        final JsonBridge jb = new JsonBridge(br, wtr);
        jb.run();
        wtr.close();
        //System.out.println(wtr);
    }
}
