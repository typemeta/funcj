package org.typemeta.funcj.codec.xml;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.functions.Functions;

import java.io.ByteArrayOutputStream;

public class XmlNoDynTypesCodecTest extends TestBase {

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
        final XmlConfig.Builder cfgBldr =
                XmlConfig.builder()
                        .dynamicTypeTags(false)
                        .failOnNoTypeConstructor(false);

        final XmlCodecCore codec = prepareCodecCore(cfgBldr, Codecs::xmlCodec);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.encode(clazz, val, baos);

        final String data = baos.toString();

        if (printData()) {
            System.out.println(data);
        }

        if (printSizes()) {
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        final XmlTypes.Config config = codec.config();
        Assert.assertFalse(data.contains(config.typeAttrName()));
    }
}
