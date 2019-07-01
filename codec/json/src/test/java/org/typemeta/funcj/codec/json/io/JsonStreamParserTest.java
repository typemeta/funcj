package org.typemeta.funcj.codec.json.io;

import org.junit.Test;

import java.io.BufferedReader;

public class JsonStreamParserTest {
    @Test
    public void test() throws Throwable {
        final BufferedReader br = FileUtils.openResource("/example.json").orElseThrow();
        final JsonStreamParser jp = new JsonStreamParser(br);

        while (jp.notEOF()) {
            //System.out.println(jp.currentEvent());
            jp.processCurrentEvent();
        }
    }
}
