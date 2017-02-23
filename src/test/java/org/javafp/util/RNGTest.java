package org.javafp.util;

import org.javafp.control.State;
import org.junit.*;

public class RNGTest {
    @Test
    public void testXorShift() {
        final String s = randomStr().eval(RNG.xorShiftRNG(1234));
        Assert.assertEquals("0.23568849406740433:5609927630774915935", s);
    }

    private static State<RNG, String> randomStr() {
        return
            RNG.nextDbl().flatMap(d ->
                RNG.nextLng().flatMap(l ->
                    State.result(d.toString() + ":" + l)
                )
            );
    }
}
