package org.typemeta.funcj.control;

import org.junit.*;

public class StateTTest {
    @Test
    public void testState() {
        final String r =
            StateT.put("efgh")
                .flatMap(u -> StateT.get())
                .flatMap(s -> StateT.put(s + "1234"))
                .flatMap(u -> StateT.get())
                .eval("abcd");

        Assert.assertEquals("efgh1234", r);
    }

    @Test
    public void testState2() {
        final String r =
            StateT.<String>get()
                .flatMap(s -> StateT.put(s + "1234"))
                .flatMap(u -> StateT.get())
                .eval("abcd");
        Assert.assertEquals("abcd1234", r);
    }
}
