package org.typemeta.funcj.json.parser;

import org.junit.Test;

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
                }).orElseThrow();
    }
}
