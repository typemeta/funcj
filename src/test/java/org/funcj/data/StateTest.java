package org.funcj.data;

import org.funcj.control.State;
import org.junit.*;

public class StateTest {
    @Test
    public void testState() {
        final String r =
            State.put("efgh")
                .then(State.get())
                .flatMap(s -> State.put(s + "1234"))
                .then(State.get())
                .eval("abcd");

        Assert.assertEquals("efgh1234", r);
    }

    @Test
    public void testState2() {
        final String r =
            State.<String>get()
                .flatMap(s -> State.put(s + "1234"))
                .then(State.get())
                .eval("abcd");
        Assert.assertEquals("abcd1234", r);
    }
}
