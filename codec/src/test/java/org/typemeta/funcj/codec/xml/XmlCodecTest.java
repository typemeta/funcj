package org.typemeta.funcj.codec.xml;

import org.junit.Assert;
import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.TestBase;

import java.io.StringReader;
import java.io.StringWriter;

public class XmlCodecTest extends TestBase {
    private static final String rootElemName = "root";

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final XmlCodecCore codec = prepareCodecCore(Codecs.xmlCodec());

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw, rootElemName);

        final StringReader sr = new StringReader(sw.toString());

        if (printData) {
            System.out.println(sw);
        }

        final T val2 = codec.decode(clazz, sr, rootElemName);

        if (!printData && !val.equals(val2)) {
            System.out.println(sw);
        }

        Assert.assertEquals(val, val2);
    }
}
