package org.typemeta.funcj.codec.json;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.typemeta.funcj.codec.TestBase;

import java.io.StringReader;
import java.io.StringWriter;

public class JmhCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonConfigImpl.BuilderImpl cfgBldr = new JsonConfigImpl.BuilderImpl();
        final JsonCodecCore codec = prepareCodecCore(cfgBldr, Codecs::jsonCodec);

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        final String data = sw.toString();

        final StringReader sr = new StringReader(data);

        codec.decode(clazz, sr);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
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
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
