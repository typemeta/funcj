package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.TestBase;

import java.io.StringReader;
import java.io.StringWriter;

public class JsonCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        final StringReader sr = new StringReader(sw.toString());

        if (printData) {
            System.out.println(sw);
        }

        final T val2 = codec.decode(clazz, sr);

        if (!printData && !val.equals(val2)) {
            System.out.println(sw);
        }

        Assert.assertEquals(val, val2);
    }
}
