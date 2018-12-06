package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;
import org.typemeta.funcj.codec.*;

import java.io.*;

public class JmhCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        final String data = sw.toString();

        final StringReader sr = new StringReader(data);

        final T val2 = codec.decode(clazz, sr);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void testAll() throws Exception {
        testCommonNulls();
        testCommon();
        testBooleanNulls();
        testBoolean();
        testByteNulls();
        testByte();
        testCharNulls();
        testChar();
        testShortNulls();
        testShort();
        testIntegerNulls();
        testInteger();
        testLongNulls();
        testLong();
        testFloatNulls();
        testFloat();
        testDoubleNulls();
        testDouble();
        testBigIntegerNulls();
        testBigInteger();
        testBigDecimalNulls();
        testBigDecimal();
        testOptionalNulls();
        testOptionalEmpty();
        testOptional();
        testNoEmptyCtor();
        testStaticCtor();
        testCustomNulls();
        testCustom();
        testEnum();
        testCollectionTypes();
        testRecursive();
    }

    public static void main(String[] args) throws RunnerException {

        final Options opt = new OptionsBuilder()
                .include(JmhCodecTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
