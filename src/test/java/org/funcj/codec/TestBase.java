package org.funcj.codec;

import org.junit.Test;

public abstract class TestBase {

    protected abstract <T> void roundTrip(T val, Class<T> clazz);

    @Test
    public void testBooleanNulls() {
        roundTrip(new TestData.BooleanData(), TestData.BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new TestData.BooleanData(TestData.Init.INIT), TestData.BooleanData.class);
    }

    @Test
    public void testByteNulls() {
        roundTrip(new TestData.ByteData(), TestData.ByteData.class);
    }

    @Test
    public void testByte() {
        roundTrip(new TestData.ByteData(TestData.Init.INIT), TestData.ByteData.class);
    }

    @Test
    public void testCharNulls() {
        roundTrip(new TestData.CharData(), TestData.CharData.class);
    }

    @Test
    public void testChar() {
        roundTrip(new TestData.CharData(TestData.Init.INIT), TestData.CharData.class);
    }

    @Test
    public void testShortNulls() {
        roundTrip(new TestData.ShortData(), TestData.ShortData.class);
    }

    @Test
    public void testShort() {
        roundTrip(new TestData.ShortData(TestData.Init.INIT), TestData.ShortData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new TestData.IntegerData(), TestData.IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new TestData.IntegerData(TestData.Init.INIT), TestData.IntegerData.class);
    }

    @Test
    public void testLongNulls() {
        roundTrip(new TestData.LongData(), TestData.LongData.class);
    }

    @Test
    public void testLong() {
        roundTrip(new TestData.LongData(TestData.Init.INIT), TestData.LongData.class);
    }

    @Test
    public void testFloatNulls() {
        roundTrip(new TestData.FloatData(), TestData.FloatData.class);
    }

    @Test
    public void testFloat() {
        roundTrip(new TestData.FloatData(TestData.Init.INIT), TestData.FloatData.class);
    }

    @Test
    public void testDoubleNulls() {
        roundTrip(new TestData.DoubleData(), TestData.DoubleData.class);
    }

    @Test
    public void testDouble() {
        roundTrip(new TestData.DoubleData(TestData.Init.INIT), TestData.DoubleData.class);
    }
}
