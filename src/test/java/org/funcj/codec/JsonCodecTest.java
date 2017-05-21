package org.funcj.codec;

import org.funcj.codec.*;
import org.funcj.json.Node;
import org.junit.*;

public class JsonCodecTest {
    final static JsonCodecCore codec = new JsonCodecCore();

    static {
        codec.initialise();
    }

    @Test
    public void roundTrip2() {
        final Example.Derived val = new Example.Derived();
        final Node node = codec.encode(Example.Derived.class, val, null);
        System.out.println(node.toJson(40));

        final Example.Derived val2 = (Example.Derived)codec.decode(Example.Derived.class, node);

        Assert.assertEquals(val, val2);
    }


    @Test
    public void roundTrip() {
        final Example.Simple val = Example.Simple.create();
        final Node node = codec.encode(Example.Simple.class, val, null);
        System.out.println(node.toJson(40));

        final Example.Simple val2 = codec.decode(Example.Simple.class, node);

        Assert.assertEquals(val, val2);
    }
}
