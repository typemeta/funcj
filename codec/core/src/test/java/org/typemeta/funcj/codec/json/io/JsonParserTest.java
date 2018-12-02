package org.typemeta.funcj.codec.json.io;

import org.junit.Test;

import java.io.BufferedReader;

public class JsonParserTest {
    @Test
    public void test() throws Throwable {
        final BufferedReader br = FileUtils.openResource("/example.json").getOrThrow();
        final JsonParser jp = new JsonParser(br, 1);

        while (jp.notEOF()) {
            //System.out.println(jp.currentEvent());
            jp.processCurrentEvent();
        }
    }
}
