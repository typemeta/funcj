package org.typemeta.funcj.codec.jsonnode;

import org.junit.*;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.JsValue;

import java.io.*;

public class JsonCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonCodecCore codec = prepareCodecCore(Codecs.jsonCodec());

        final JsValue jsv = codec.encode(clazz, val);

        if (printData()) {
            System.out.println(jsv);
        }

        final String data = jsv.toString();

        if (printSizes()) {
            System.out.println("Encoded JSON " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        final T val2 = codec.decode(clazz, jsv);

        if (!printData() && !val.equals(val2)) {
            System.out.println(jsv);
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
