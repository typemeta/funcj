package org.typemeta.funcj.codec;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

public abstract class TestBase {

    protected static final boolean printData = false;
    protected static final boolean printSizes = false;

    protected abstract <T> void roundTrip(T val, Class<T> clazz) throws Exception;

    protected static <
            IN extends CodecFormat.Input<IN>,
            OUT extends CodecFormat.Output<OUT>,
            CFG extends CodecConfig,
            CC extends CodecCore<IN, OUT, CFG>
             > CC prepareCodecCore(CC core) {
        core.registerCodec(TestTypes.Custom.class)
                .field("colour", c -> c.colour, TestTypes.Custom.Colour.class)
                .field("date", c -> c.date, LocalDate.class)
                .field("flag", c -> c.flag, Boolean.class)
                .field("name", c -> c.name, String.class)
                .field("age", c -> c.age, Double.class)
                .map(args -> new TestTypes.Custom(
                        (TestTypes.Custom.Colour)args[0],
                        (LocalDate)args[1],
                        (Boolean)args[2],
                        (String)args[3],
                        (Double)args[4]));

        core.registerNoArgsCtor(TestTypes.NoEmptyCtor.class, () -> TestTypes.NoEmptyCtor.create(false));

        core.registerArgArrayCtor(TestTypes.StaticCtor.class, args -> TestTypes.StaticCtor.create((boolean)args[0]));

        core.config().registerAllowedPackage(TestTypes.class.getPackage());
        return core;
    }

    @Test
    public void testCommonNulls() throws Exception {
        roundTrip(new TestTypes.CommonData(), TestTypes.CommonData.class);
    }

    @Test
    public void testCommon() throws Exception {
        roundTrip(new TestTypes.CommonData(TestTypes.Init.INIT), TestTypes.CommonData.class);
    }

    @Test
    public void testBooleanNulls() throws Exception {
        roundTrip(new TestTypes.BooleanData(), TestTypes.BooleanData.class);
    }

    @Test
    public void testBoolean() throws Exception {
        roundTrip(new TestTypes.BooleanData(TestTypes.Init.INIT), TestTypes.BooleanData.class);
    }

    @Test
    public void testByteNulls() throws Exception {
        roundTrip(new TestTypes.ByteData(), TestTypes.ByteData.class);
    }

    @Test
    public void testByte() throws Exception {
        roundTrip(new TestTypes.ByteData(TestTypes.Init.INIT), TestTypes.ByteData.class);
    }

    @Test
    public void testCharNulls() throws Exception {
        roundTrip(new TestTypes.CharData(), TestTypes.CharData.class);
    }

    @Test
    public void testChar() throws Exception {
        roundTrip(new TestTypes.CharData(TestTypes.Init.INIT), TestTypes.CharData.class);
    }

    @Test
    public void testShortNulls() throws Exception {
        roundTrip(new TestTypes.ShortData(), TestTypes.ShortData.class);
    }

    @Test
    public void testShort() throws Exception {
        roundTrip(new TestTypes.ShortData(TestTypes.Init.INIT), TestTypes.ShortData.class);
    }

    @Test
    public void testIntegerNulls() throws Exception {
        roundTrip(new TestTypes.IntegerData(), TestTypes.IntegerData.class);
    }

    @Test
    public void testInteger() throws Exception {
        roundTrip(new TestTypes.IntegerData(TestTypes.Init.INIT), TestTypes.IntegerData.class);
    }

    @Test
    public void testLongNulls() throws Exception {
        roundTrip(new TestTypes.LongData(), TestTypes.LongData.class);
    }

    @Test
    public void testLong() throws Exception {
        roundTrip(new TestTypes.LongData(TestTypes.Init.INIT), TestTypes.LongData.class);
    }

    @Test
    public void testFloatNulls() throws Exception {
        roundTrip(new TestTypes.FloatData(), TestTypes.FloatData.class);
    }

    @Test
    public void testFloat() throws Exception {
        roundTrip(new TestTypes.FloatData(TestTypes.Init.INIT), TestTypes.FloatData.class);
    }

    @Test
    public void testDoubleNulls() throws Exception {
        roundTrip(new TestTypes.DoubleData(), TestTypes.DoubleData.class);
    }

    @Test
    public void testDouble() throws Exception {
        roundTrip(new TestTypes.DoubleData(TestTypes.Init.INIT), TestTypes.DoubleData.class);
    }

    @Test
    public void testBigIntegerNulls() throws Exception {
        roundTrip(new TestTypes.BigIntegerData(), TestTypes.BigIntegerData.class);
    }

    @Test
    public void testBigInteger() throws Exception {
        roundTrip(new TestTypes.BigIntegerData(TestTypes.Init.INIT), TestTypes.BigIntegerData.class);
    }

    @Test
    public void testBigDecimalNulls() throws Exception {
        roundTrip(new TestTypes.BigDecimalData(), TestTypes.BigDecimalData.class);
    }

    @Test
    public void testBigDecimal() throws Exception {
        roundTrip(new TestTypes.BigDecimalData(TestTypes.Init.INIT), TestTypes.BigDecimalData.class);
    }

    @Test
    public void testOptionalNulls() throws Exception {
        roundTrip(TestTypes.HasOptional.<Integer>create(), TestTypes.HasOptional.class);
    }

    @Test
    public void testOptionalEmpty() throws Exception {
        roundTrip(new TestTypes.HasOptional<Integer>(Optional.empty(), Optional.empty()), TestTypes.HasOptional.class);
    }

    @Test
    public void testOptional() throws Exception {
        roundTrip(new TestTypes.HasOptional<Integer>(1234, "abcd"), TestTypes.HasOptional.class);
    }

    @Test
    public void testNoEmptyCtor() throws Exception {
        roundTrip(TestTypes.NoEmptyCtor.create(true), TestTypes.NoEmptyCtor.class);
    }

    @Test
    public void testStaticCtor() throws Exception {
        roundTrip(TestTypes.StaticCtor.create(true), TestTypes.StaticCtor.class);
    }

    @Test
    public void testRecursive() throws Exception {
        final TestTypes.Recursive rec = new TestTypes.Recursive(null, 0);
        roundTrip(rec, TestTypes.Recursive.class);

        final TestTypes.Recursive rec2 = new TestTypes.Recursive(rec, 1);
        roundTrip(rec2, TestTypes.Recursive.class);

        final TestTypes.Recursive rec3 = new TestTypes.Recursive(rec2, 2);
        roundTrip(rec3, TestTypes.Recursive.class);
    }

    @Test
    public void testCustomNulls() throws Exception {
        roundTrip(new TestTypes.Custom(), TestTypes.Custom.class);
    }

    @Test
    public void testCustom() throws Exception {
        roundTrip(new TestTypes.Custom(TestTypes.Init.INIT), TestTypes.Custom.class);
    }

    @Test
    public void testEnum() throws Exception {
        roundTrip(new TestTypes.TypeWithEnum(TestTypes.EnumType.VALUE2), TestTypes.TypeWithEnum.class);
    }

    @Test
    public void testCollectionTypes() throws Exception {
        roundTrip(new TestTypes.CollTypes(), TestTypes.CollTypes.class);
    }

    @Test
    public void testInterface() throws Exception {
        roundTrip(new TestTypes.SomeClass("bleh"), TestTypes.SomeInterface.class);
    }
}
