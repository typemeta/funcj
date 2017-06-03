package org.funcj.codec;

import org.funcj.codec.json.JsonCodecCore;
import org.funcj.json.Node;
import org.junit.Assert;

import static java.lang.System.out;

public class JsonCodecTest extends TestBase {
    final static JsonCodecCore codec = new JsonCodecCore();

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final Node node = codec.encode(clazz, val);
        out.println(node.toJson(40));

        final T val2 = codec.decode(clazz, node);

        Assert.assertEquals(val, val2);
    }
}
