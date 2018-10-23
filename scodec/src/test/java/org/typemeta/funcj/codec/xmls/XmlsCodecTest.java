package org.typemeta.funcj.codec.xmls;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;

import java.io.StringReader;
import java.io.StringWriter;

public class XmlsCodecTest extends TestBase {
    final static XmlCodecCore codec = Codecs.xmlCodec();

    public static final String rootElemName = "root";

    static {
        codec.registerTypeConstructor(TestTypes.NoEmptyCtor.class, () -> TestTypes.NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
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
