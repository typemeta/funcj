package org.typemeta.funcj.codec;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

public abstract class TestBase {

    protected static final boolean printData = false;

    protected abstract <T> void roundTrip(T val, Class<T> clazz);

    @Test
    public void testCommonNulls() {
        roundTrip(new TestTypes.CommonData(), TestTypes.CommonData.class);
    }

    @Test
    public void testCommon() {
        roundTrip(new TestTypes.CommonData(TestTypes.Init.INIT), TestTypes.CommonData.class);
    }

    @Test
    public void testBooleanNulls() {
        roundTrip(new TestTypes.BooleanData(), TestTypes.BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new TestTypes.BooleanData(TestTypes.Init.INIT), TestTypes.BooleanData.class);
    }

    @Test
    public void testByteNulls() {
        roundTrip(new TestTypes.ByteData(), TestTypes.ByteData.class);
    }

    @Test
    public void testByte() {
        roundTrip(new TestTypes.ByteData(TestTypes.Init.INIT), TestTypes.ByteData.class);
    }

    @Test
    public void testCharNulls() {
        roundTrip(new TestTypes.CharData(), TestTypes.CharData.class);
    }

    @Test
    public void testChar() {
        roundTrip(new TestTypes.CharData(TestTypes.Init.INIT), TestTypes.CharData.class);
    }

    @Test
    public void testShortNulls() {
        roundTrip(new TestTypes.ShortData(), TestTypes.ShortData.class);
    }

    @Test
    public void testShort() {
        roundTrip(new TestTypes.ShortData(TestTypes.Init.INIT), TestTypes.ShortData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new TestTypes.IntegerData(), TestTypes.IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new TestTypes.IntegerData(TestTypes.Init.INIT), TestTypes.IntegerData.class);
    }

    @Test
    public void testLongNulls() {
        roundTrip(new TestTypes.LongData(), TestTypes.LongData.class);
    }

    @Test
    public void testLong() {
        roundTrip(new TestTypes.LongData(TestTypes.Init.INIT), TestTypes.LongData.class);
    }

    @Test
    public void testFloatNulls() {
        roundTrip(new TestTypes.FloatData(), TestTypes.FloatData.class);
    }

    @Test
    public void testFloat() {
        roundTrip(new TestTypes.FloatData(TestTypes.Init.INIT), TestTypes.FloatData.class);
    }

    @Test
    public void testDoubleNulls() {
        roundTrip(new TestTypes.DoubleData(), TestTypes.DoubleData.class);
    }

    @Test
    public void testDouble() {
        roundTrip(new TestTypes.DoubleData(TestTypes.Init.INIT), TestTypes.DoubleData.class);
    }

    @Test
    public void testOptionalNulls() {
        roundTrip(new TestTypes.HasOptional<Integer>(), TestTypes.HasOptional.class);
    }

    @Test
    public void testOptionalEmpty() {
        roundTrip(new TestTypes.HasOptional<Integer>(Optional.empty(), Optional.empty()), TestTypes.HasOptional.class);
    }

    @Test
    public void testOptional() {
        roundTrip(new TestTypes.HasOptional<Integer>(1234, "abcd"), TestTypes.HasOptional.class);
    }

    @Test
    public void testNoEmptyCtor() {
        roundTrip(TestTypes.NoEmptyCtor.create(true), TestTypes.NoEmptyCtor.class);
    }

    public static <E> void registerCustomCodec(CodecCore<E> core) {
        core.registerCodec(TestTypes.Custom.class)
                .nullField("colour", c -> c.colour, TestTypes.Custom.Colour.class)
                .nullField("date", c -> c.date, LocalDate.class)
                .field("flag", c -> c.flag, Boolean.class)
                .nullField("name", c -> c.name, String.class)
                .field("age", c -> c.age, Double.class)
                .map(args -> new TestTypes.Custom(
                        (TestTypes.Custom.Colour)args[0],
                        (LocalDate)args[1],
                        (Boolean)args[2],
                        (String)args[3],
                        (Double)args[4]));
    }

    @Test
    public void testCustomNulls() {
        roundTrip(new TestTypes.Custom(), TestTypes.Custom.class);
    }

    @Test
    public void testCustom() {
        roundTrip(new TestTypes.Custom(TestTypes.Init.INIT), TestTypes.Custom.class);
    }

    @Test
    public void testRecursive() {
        final TestTypes.Recursive rec = new TestTypes.Recursive(null, 0);
        roundTrip(rec, TestTypes.Recursive.class);

        final TestTypes.Recursive rec2 = new TestTypes.Recursive(rec, 1);
        roundTrip(rec2, TestTypes.Recursive.class);

        final TestTypes.Recursive rec3 = new TestTypes.Recursive(rec2, 2);
        roundTrip(rec3, TestTypes.Recursive.class);
    }
}
