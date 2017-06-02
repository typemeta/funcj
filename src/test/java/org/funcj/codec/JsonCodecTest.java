package org.funcj.codec;

import org.funcj.codec.TestData.*;
import org.funcj.codec.json.JsonCodecCore;
import org.funcj.json.Node;
import org.junit.*;

import static java.lang.System.out;

public class JsonCodecTest {
    final static JsonCodecCore codec = new JsonCodecCore();

    private <T> void roundTrip(T val, Class<T> clazz) {
        final Node node = codec.encode(clazz, val);
        out.println(node.toJson(40));

        final T val2 = codec.decode(clazz, node);

        Assert.assertEquals(val, val2);
    }

    @Test
    public void testBooleanNulls() {
        roundTrip(new BooleanData(), BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new BooleanData(Init.INIT), BooleanData.class);
    }

    @Test
    public void testByteNulls() {
        roundTrip(new ByteData(), ByteData.class);
    }

    @Test
    public void testByte() {
        roundTrip(new ByteData(Init.INIT), ByteData.class);
    }

    @Test
    public void testCharNulls() {
        roundTrip(new CharData(), CharData.class);
    }

    @Test
    public void testChar() {
        roundTrip(new CharData(Init.INIT), CharData.class);
    }

    @Test
    public void testShortNulls() {
        roundTrip(new ShortData(), ShortData.class);
    }

    @Test
    public void testShort() {
        roundTrip(new ShortData(Init.INIT), ShortData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new IntegerData(), IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new IntegerData(Init.INIT), IntegerData.class);
    }

    @Test
    public void testLongNulls() {
        roundTrip(new LongData(), LongData.class);
    }

    @Test
    public void testLong() {
        roundTrip(new LongData(Init.INIT), LongData.class);
    }

    @Test
    public void testFloatNulls() {
        roundTrip(new FloatData(), FloatData.class);
    }

    @Test
    public void testFloat() {
        roundTrip(new FloatData(Init.INIT), FloatData.class);
    }

    @Test
    public void testDoubleNulls() {
        roundTrip(new DoubleData(), DoubleData.class);
    }

    @Test
    public void testDouble() {
        roundTrip(new DoubleData(Init.INIT), DoubleData.class);
    }
}
