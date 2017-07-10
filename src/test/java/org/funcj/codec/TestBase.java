package org.funcj.codec;

import org.funcj.codec.TestDataBase.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

public abstract class TestBase {

    protected static final boolean printData = false;

    protected abstract <T> void roundTrip(T val, Class<T> clazz);

    @Test
    public void testCommonNulls() {
        roundTrip(new CommonData(), CommonData.class);
    }

    @Test
    public void testCommon() {
        roundTrip(new CommonData(Init.INIT), CommonData.class);
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

    @Test
    public void testOptionalNulls() {
        roundTrip(new HasOptional<Integer>(), HasOptional.class);
    }

    @Test
    public void testOptionalEmpty() {
        roundTrip(new HasOptional<Integer>(Optional.empty(), Optional.empty()), HasOptional.class);
    }

    @Test
    public void testOptional() {
        roundTrip(new HasOptional<Integer>(1234, "abcd"), HasOptional.class);
    }

    @Test
    public void testNoEmptyCtor() {
        roundTrip(NoEmptyCtor.create(true), NoEmptyCtor.class);
    }

    static <E> void registerLocalDateCodec(CodecCore<E> core) {
        core.codecBuilder(LocalDate.class)
                .field("year", LocalDate::getYear, Integer.class)
                .field("month", LocalDate::getMonthValue, Integer.class)
                .field("day", LocalDate::getDayOfMonth, Integer.class)
                .map(LocalDate::of);
    }

    static <E> void registerCustomCodec(CodecCore<E> core) {
        core.codecBuilder(Custom.class)
                .nullField("colour", c -> c.colour, Custom.Colour.class)
                .nullField("date", c -> c.date, LocalDate.class)
                .field("flag", c -> c.flag, Boolean.class)
                .nullField("name", c -> c.name, String.class)
                .field("age", c -> c.age, Double.class)
                .map(args -> new Custom(
                        (Custom.Colour)args[0],
                        (LocalDate)args[1],
                        (Boolean)args[2],
                        (String)args[3],
                        (Double)args[4]));
    }

    @Test
    public void testCustomNulls() {
        roundTrip(new Custom(), Custom.class);
    }

    @Test
    public void testCustom() {
        roundTrip(new Custom(Init.INIT), Custom.class);
    }

    @Test
    public void testRecursive() {
        final Recursive rec = new Recursive(null, 0);
        roundTrip(rec, Recursive.class);

        final Recursive rec2 = new Recursive(rec, 1);
        roundTrip(rec2, Recursive.class);

        final Recursive rec3 = new Recursive(rec2, 2);
        roundTrip(rec3, Recursive.class);
    }
}
