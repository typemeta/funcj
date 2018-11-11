package org.typemeta.funcj.codec.json.io;

import org.junit.Test;
import org.typemeta.funcj.codec.json.JsonCodec;

public class JsonTokeniserTest {
    @Test
    public void test() throws Throwable {
        FileUtils.openResource("/example.json")
                .map(JsonTokeniser::new)
                .map(jt -> {
                    JsonCodec.Input.Event ev;
                    while ((ev = jt.getNextEvent()) != JsonCodec.Input.Event.Type.EOF) {
                        //System.out.println(ev);
                    }
                    return 0;
                }).getOrThrow();
    }
}
