package org.typemeta.funcj.codec;

import org.junit.Test;
import org.typemeta.funcj.codec.misc.SimpleType;
import org.typemeta.funcj.codec.utils.CodecException;

import java.time.LocalDate;
import java.util.*;

import static org.typemeta.funcj.codec.TestTypes.*;

public abstract class TestBase {

    protected boolean printData() {
        return false;
    }

    protected boolean printSizes() {
        return false;
    }

    protected abstract <T> void roundTrip(T val, Class<T> clazz) throws Exception;

    protected static <IN, OUT, CFG extends CodecConfig, CC extends CodecCore<IN, OUT, CFG>>
    CC prepareCodecCore(CC core) {

        core.config().registerAllowedPackage(TestTypes.class.getPackage());

        core.registerCodecWithArgMap(Custom.class)
                .field("colour", c -> c.colour, Custom.Colour.class)
                .field("date", c -> c.dates, List.class, LocalDate.class)
                .field("flag", c -> c.flag, Boolean.class)
                .field("name", c -> c.name, String.class)
                .field("age", c -> c.age, Double.class)
                .construct(args -> new Custom(
                        (Custom.Colour)args.get("colour"),
                        (List<LocalDate>)args.get("date"),
                        (Boolean)args.get("flag"),
                        (String)args.get("name"),
                        (Double)args.get("age")));

        core.registerNoArgsCtor(NoEmptyCtor.class, () -> NoEmptyCtor.create(false));

        core.registerArgArrayCtor(StaticCtor.class, args -> StaticCtor.create((boolean)args[0]));

        return core;
    }

    @Test
    public void testCommonNulls() throws Exception {
        roundTrip(new CommonData(), CommonData.class);
    }

    @Test
    public void testCommon() throws Exception {
        roundTrip(new CommonData(Init.INIT), CommonData.class);
    }

    @Test
    public void testBooleanNulls() throws Exception {
        roundTrip(new BooleanData(), BooleanData.class);
    }

    @Test
    public void testBoolean() throws Exception {
        roundTrip(new BooleanData(Init.INIT), BooleanData.class);
    }

    @Test
    public void testByteNulls() throws Exception {
        roundTrip(new ByteData(), ByteData.class);
    }

    @Test
    public void testByte() throws Exception {
        roundTrip(new ByteData(Init.INIT), ByteData.class);
    }

    @Test
    public void testCharNulls() throws Exception {
        roundTrip(new CharData(), CharData.class);
    }

    @Test
    public void testChar() throws Exception {
        roundTrip(new CharData(Init.INIT), CharData.class);
    }

    @Test
    public void testShortNulls() throws Exception {
        roundTrip(new ShortData(), ShortData.class);
    }

    @Test
    public void testShort() throws Exception {
        roundTrip(new ShortData(Init.INIT), ShortData.class);
    }

    @Test
    public void testIntegerNulls() throws Exception {
        roundTrip(new IntegerData(), IntegerData.class);
    }

    @Test
    public void testInteger() throws Exception {
        roundTrip(new IntegerData(Init.INIT), IntegerData.class);
    }

    @Test
    public void testLongNulls() throws Exception {
        roundTrip(new LongData(), LongData.class);
    }

    @Test
    public void testLong() throws Exception {
        roundTrip(new LongData(Init.INIT), LongData.class);
    }

    @Test
    public void testFloatNulls() throws Exception {
        roundTrip(new FloatData(), FloatData.class);
    }

    @Test
    public void testFloat() throws Exception {
        roundTrip(new FloatData(Init.INIT), FloatData.class);
    }

    @Test
    public void testDoubleNulls() throws Exception {
        roundTrip(new DoubleData(), DoubleData.class);
    }

    @Test
    public void testDouble() throws Exception {
        roundTrip(new DoubleData(Init.INIT), DoubleData.class);
    }

    @Test
    public void testBigIntegerNulls() throws Exception {
        roundTrip(new BigIntegerData(), BigIntegerData.class);
    }

    @Test
    public void testBigInteger() throws Exception {
        roundTrip(new BigIntegerData(Init.INIT), BigIntegerData.class);
    }

    @Test
    public void testBigDecimalNulls() throws Exception {
        roundTrip(new BigDecimalData(), BigDecimalData.class);
    }

    @Test
    public void testBigDecimal() throws Exception {
        roundTrip(new BigDecimalData(Init.INIT), BigDecimalData.class);
    }

    @Test
    public void testOptionalNulls() throws Exception {
        roundTrip(HasOptional.<Integer>create(), HasOptional.class);
    }

    @Test
    public void testOptionalEmpty() throws Exception {
        roundTrip(new TestTypes.HasOptional<Integer>(Optional.empty(), Optional.empty()), HasOptional.class);
    }

    @Test
    public void testOptional() throws Exception {
        roundTrip(new TestTypes.HasOptional<Integer>(1234, "abcd"), HasOptional.class);
    }

    @Test
    public void testNoEmptyCtor() throws Exception {
        roundTrip(NoEmptyCtor.create(true), NoEmptyCtor.class);
    }

    @Test
    public void testStaticCtor() throws Exception {
        roundTrip(StaticCtor.create(true), StaticCtor.class);
    }

    @Test
    public void testRecursive() throws Exception {
        final Recursive rec = new Recursive(null, 0);
        roundTrip(rec, Recursive.class);

        final Recursive rec2 = new Recursive(rec, 1);
        roundTrip(rec2, Recursive.class);

        final Recursive rec3 = new Recursive(rec2, 2);
        roundTrip(rec3, Recursive.class);
    }

    @Test
    public void testCustomNulls() throws Exception {
        roundTrip(new Custom(Init.INIT), Custom.class);
    }

    @Test
    public void testCustom() throws Exception {
        roundTrip(new Custom(Init.INIT), Custom.class);
    }

    @Test
    public void testEnum() throws Exception {
        roundTrip(new TypeWithEnum(EnumType.VALUE2), TypeWithEnum.class);
    }

    @Test
    public void testCollectionTypes() throws Exception {
        roundTrip(new CollTypes(), CollTypes.class);
    }

    @Test
    public void testInterface() throws Exception {
        roundTrip(new SomeClass("bleh"), SomeInterface.class);
    }

    @Test(expected = CodecException.class)
    public void testShouldFail() throws Exception {
        roundTrip(new ShouldFail(new SimpleType("")), ShouldFail.class);
    }
}
