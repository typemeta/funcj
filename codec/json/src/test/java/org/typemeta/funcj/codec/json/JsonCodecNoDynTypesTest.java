package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.functions.Functions;

import java.io.StringWriter;

public class JsonCodecNoDynTypesTest extends TestBase {

    protected static <IN, OUT, CFG extends CodecConfig, CORE extends CodecCore<IN, OUT, CFG>>
    CORE prepareCodecCore(
            CodecConfig.Builder<?, CFG> cfgBldr,
            Functions.F<CodecConfig.Builder<?, CFG>, CORE> coreBldr
    ) {
        cfgBldr.registerAllowedPackage(TestTypes.class.getPackage());
        return TestBase.prepareCodecCore(cfgBldr, coreBldr);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonConfig.Builder cfgBldr =
                JsonConfig.builder()
                        .dynamicTypeTags(false)
                        .failOnNoTypeConstructor(false);

        final JsonCodecCore codec = prepareCodecCore(cfgBldr, Codecs::jsonCodec);

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        if (printData()) {
            System.out.println(sw);
        }

        final String data = sw.toString();

        if (printSizes()) {
            System.out.println("Encoded JSON " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        final JsonTypes.Config config = codec.config();
        Assert.assertFalse(data.contains(config.typeFieldName()));
    }
}
