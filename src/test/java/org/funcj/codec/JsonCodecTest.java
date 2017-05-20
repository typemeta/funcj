package org.funcj.codec;

import org.funcj.json.Node;
import org.junit.*;

public class JsonCodecTest {
    final Codec.Registry<Node> codec = new JsonCodec();

    @Test
    public void roundTrip() {
        final Example.Derived val = new Example.Derived();
        final Node node = codec.encode(val, null);
        System.out.println(node.toJson(40));

        final Example.Derived val2 = codec.decode(node, Example.Derived.class);

        Assert.assertEquals(val, val2);
    }
}
