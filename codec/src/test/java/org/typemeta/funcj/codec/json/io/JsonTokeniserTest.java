package org.typemeta.funcj.codec.json.io;

import org.junit.Test;
import org.typemeta.funcj.codec.json.JsonTypes;

public class JsonTokeniserTest {
    @Test
    public void test() throws Throwable {
        FileUtils.openResource("/example.json")
                .map(JsonTokeniser::new)
                .map(jt -> {
                    JsonTypes.InStream.Event ev;
                    while ((ev = jt.getNextEvent()) != JsonTypes.InStream.Event.Type.EOF) {
                        //System.out.println(ev);
                    }
                    return 0;
                }).getOrThrow();
    }
}
