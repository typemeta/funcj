package org.funcj.codec;

import org.funcj.json.Node;
import org.junit.*;

public class JsonCodecTest {
    final static Codec.DynamicCodec<Node> codec = new JsonCodec();

    static {
        codec.initialise();
    }

    @Test
    public void roundTrip2() {
        final Example.Derived val = new Example.Derived();
        final Node node = codec.encode(val, null);
        System.out.println(node.toJson(40));

        final Example.Derived val2 = (Example.Derived)codec.decode(node, Example.Derived.class);

        Assert.assertEquals(val, val2);
    }


    @Test
    public void roundTrip() {
        final Example.Simple val = new Example.Simple();
        final Node node = codec.encode(val, null);
        System.out.println(node.toJson(40));

        final Example.Simple val2 = (Example.Simple)codec.decode(node, Example.Simple.class);

        Assert.assertEquals(val, val2);
    }
}
