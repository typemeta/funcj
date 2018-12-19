package org.typemeta.funcj.codec.json;


import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import java.io.*;

public class JsonCodecNoDynTypesTest extends TestBase {

    protected static <IN, OUT, CFG extends CodecConfig, CC extends CodecCore<IN, OUT, CFG>>
    CC prepareCodecCore(CC core) {
        core.config().registerAllowedPackage(TestTypes.class.getPackage());
        return core;
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());
        final JsonTypes.Config config = codec.config();
        config.dynamicTypeTags(false);
        config.failOnNoTypeConstructor(false);

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        if (printData()) {
            System.out.println(sw);
        }

        final String data = sw.toString();

        if (printSizes()) {
            System.out.println("Encoded JSON " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        Assert.assertFalse(data.contains(config.typeFieldName()));
    }
}
