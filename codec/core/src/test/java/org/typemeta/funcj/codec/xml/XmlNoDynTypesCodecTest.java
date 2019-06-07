package org.typemeta.funcj.codec.xml;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;

import java.io.*;

public class XmlNoDynTypesCodecTest extends TestBase {

    protected static <IN, OUT, CFG extends CodecConfig, CC extends CodecCore<IN, OUT, CFG>>
    CC prepareCodecCore(CC core) {
        core.config().registerAllowedPackage(TestTypes.class.getPackage());
        return core;
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final XmlCodecCore codec = prepareCodecCore(Codecs.xmlCodec());
        final XmlTypes.Config config = codec.config();
        config.dynamicTypeTags(false);
        config.failOnNoTypeConstructor(false);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.encode(clazz, val, baos);

        final String data = baos.toString();

        if (printData()) {
            System.out.println(data);
        }

        if (printSizes()) {
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        Assert.assertFalse(data.contains(config.typeAttrName()));
    }
}
