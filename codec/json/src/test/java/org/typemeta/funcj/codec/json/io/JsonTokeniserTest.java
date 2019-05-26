package org.typemeta.funcj.codec.json.io;

import org.junit.Test;
import org.typemeta.funcj.json.parser.JsonEvent;
import org.typemeta.funcj.json.parser.JsonTokeniser;

public class JsonTokeniserTest {
    @Test
    public void test() throws Throwable {
        FileUtils.openResource("/example.json")
                .map(JsonTokeniser::new)
                .map(jt -> {
                    JsonEvent ev;
                    while ((ev = jt.getNextEvent()) != JsonEvent.Type.EOF) {
                        //System.out.println(ev);
                    }
                    return 0;
                }).getOrThrow();
    }
}
