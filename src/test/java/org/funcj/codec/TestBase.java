package org.funcj.codec;

import org.junit.Test;

public abstract class TestBase {

    protected abstract <T> void roundTrip(T val, Class<T> clazz);

    @Test
    public void testBooleanNulls() {
        roundTrip(new DataBase.BooleanData(), DataBase.BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new DataBase.BooleanData(DataBase.Init.INIT), DataBase.BooleanData.class);
    }

    @Test
    public void testByteNulls() {
        roundTrip(new DataBase.ByteData(), DataBase.ByteData.class);
    }

    @Test
    public void testByte() {
        roundTrip(new DataBase.ByteData(DataBase.Init.INIT), DataBase.ByteData.class);
    }

    @Test
    public void testCharNulls() {
        roundTrip(new DataBase.CharData(), DataBase.CharData.class);
    }

    @Test
    public void testChar() {
        roundTrip(new DataBase.CharData(DataBase.Init.INIT), DataBase.CharData.class);
    }

    @Test
    public void testShortNulls() {
        roundTrip(new DataBase.ShortData(), DataBase.ShortData.class);
    }

    @Test
    public void testShort() {
        roundTrip(new DataBase.ShortData(DataBase.Init.INIT), DataBase.ShortData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new DataBase.IntegerData(), DataBase.IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new DataBase.IntegerData(DataBase.Init.INIT), DataBase.IntegerData.class);
    }

    @Test
    public void testLongNulls() {
        roundTrip(new DataBase.LongData(), DataBase.LongData.class);
    }

    @Test
    public void testLong() {
        roundTrip(new DataBase.LongData(DataBase.Init.INIT), DataBase.LongData.class);
    }

    @Test
    public void testFloatNulls() {
        roundTrip(new DataBase.FloatData(), DataBase.FloatData.class);
    }

    @Test
    public void testFloat() {
        roundTrip(new DataBase.FloatData(DataBase.Init.INIT), DataBase.FloatData.class);
    }

    @Test
    public void testDoubleNulls() {
        roundTrip(new DataBase.DoubleData(), DataBase.DoubleData.class);
    }

    @Test
    public void testDouble() {
        roundTrip(new DataBase.DoubleData(DataBase.Init.INIT), DataBase.DoubleData.class);
    }
}
