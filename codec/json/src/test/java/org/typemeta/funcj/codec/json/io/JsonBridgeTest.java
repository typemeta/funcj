package org.typemeta.funcj.codec.json.io;

import org.junit.Test;

import java.io.*;

public class JsonBridgeTest {
    @Test
    public void test() throws Throwable {
        final StringWriter wtr = new StringWriter();
        final BufferedReader br = FileUtils.openResource("/example.json").getOrThrow();
        final JsonBridge jb = new JsonBridge(br, wtr);
        jb.run();
        wtr.close();
        //System.out.println(wtr);
    }
}
