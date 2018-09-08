package org.typemeta.funcj.codec.jsons;

import org.junit.Test;

public class JsonParserTest {
    @Test
    public void test() throws Exception {
        FileUtils.openResource("/example.json")
                .map(JsonParser::new)
                .map(jp -> {
                    while (jp.notEOF()) {
                        System.out.println(jp.currEvent());
                        jp.next();
                    }
                    return 0;
                }).getOrThrow();
    }
}
