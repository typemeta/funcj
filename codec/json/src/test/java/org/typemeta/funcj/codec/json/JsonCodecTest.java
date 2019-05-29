package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.junit.Test;
import org.typemeta.funcj.codec.TestBase;
import org.typemeta.funcj.codec.TestTypes;

import java.io.StringReader;
import java.io.StringWriter;

public class JsonCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());

        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        if (printData()) {
            System.out.println(sw);
        }

        final String data = sw.toString();

        if (printSizes()) {
            System.out.println("Encoded JSON " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        final StringReader sr = new StringReader(data);

        final T val2 = codec.decode(clazz, sr);

        if (!printData() && !val.equals(val2)) {
            System.out.println(sw);
        }

        Assert.assertEquals(val, val2);
    }

    @Test
    public void testDontFailOnUnrecognisedFields() {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());
        codec.config().failOnUnrecognisedFields(false);
        final TestTypes.Custom val = new TestTypes.Custom(TestTypes.Init.INIT);

        final StringWriter sw = new StringWriter();
        codec.encode(TestTypes.Custom.class, val, sw);

        final String raw = sw.toString();
        final String raw2 = raw.replace("\"flag\"", "\"test\" : {\"a\": [12.34, \"z\"] }, \"flag\"");

        final TestTypes.Custom val2 = codec.decode(TestTypes.Custom.class, new StringReader(raw2));

        Assert.assertEquals(val, val2);
    }
}
