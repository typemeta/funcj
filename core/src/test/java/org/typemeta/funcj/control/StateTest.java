package org.typemeta.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;
import org.typemeta.funcj.data.Unit;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.control.State.*;

@RunWith(JUnitQuickcheck.class)
public class StateTest {
    @Property
    public void testState(String a, String b, String c) {
        final String r =
            put(a)
                .flatMap(u -> get())
                .flatMap(s -> put(s + b))
                .flatMap(u -> get())
                .eval(c);

        assertEquals(a+b, r);
    }

    @Property
    public void testState2(String a, String b) {
        final String r =
            State.<String>get()
                .flatMap(s -> put(s + a))
                .flatMap(u -> get())
                .eval(b);
        assertEquals(b+a, r);
    }

    static class Utils {

    }

    @Property
    public void kleisli(String a, String b) {
        final Kleisli<String, String, String> get = Kleisli.of(u -> State.<String>get());
        final Kleisli<String, String, Unit> wrap = get.andThen(s -> put("@" + s + "$"));
        final String c = wrap.run(a).exec(b);
        assertEquals("@" + b + "$", c);
    }
}
