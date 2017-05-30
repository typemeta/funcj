package org.funcj.codec;

import org.funcj.codec.TestData.*;
import org.funcj.json.Node;
import org.junit.*;

import static java.lang.System.out;

public class JsonCodecTest {
    final static JsonCodecCore codec = new JsonCodecCore();

    @Test
    public void testBooleanNulls() {
        roundTrip(new BooleanData(), BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new BooleanData(Init.INIT), BooleanData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new IntegerData(), IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new IntegerData(Init.INIT), IntegerData.class);
    }

    private <T> void roundTrip(T val, Class<T> clazz) {
        final Node node = codec.encode(clazz, val, null);
        out.println(node.toJson(40));

        final T val2 = codec.decode(clazz, node);

        Assert.assertEquals(val, val2);
    }
}
