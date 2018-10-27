package org.typemeta.funcj.codec.json;

import org.junit.Test;
import org.typemeta.funcj.codec.json.io.JsonParser;

import java.io.BufferedReader;

public class JsonParserTest {
    @Test
    public void test() throws Exception {
        final BufferedReader br = FileUtils.openResource("/example.json").getOrThrow();
        final JsonParser jp = new JsonParser(br);

        while (jp.notEOF()) {
            //System.out.println(jp.currentEvent());
            jp.processCurrentEvent();
        }
    }
}
