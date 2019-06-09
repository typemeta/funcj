package org.typemeta.funcj.codec.jsonnode;

import org.junit.Assert;
import org.junit.Test;
import org.typemeta.funcj.codec.TestBase;
import org.typemeta.funcj.codec.TestTypes;
import org.typemeta.funcj.codec.json.Codecs;
import org.typemeta.funcj.json.model.*;

import java.util.ArrayList;
import java.util.List;

public class JsonCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JsonNodeCodecCore codec = prepareCodecCore(Codecs.jsonNodeCodec());

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
        final JsonNodeCodecCore codec = prepareCodecCore(Codecs.jsonNodeCodec());
        codec.config().failOnUnrecognisedFields(false);
        final TestTypes.Custom val = new TestTypes.Custom(TestTypes.Init.INIT);

        final JsObject jso = codec.encode(TestTypes.Custom.class, val).asObject();

        final List<JsObject.Field> fields = new ArrayList<>();
        fields.add(JSAPI.field("dummy", JSAPI.num(-999)));
        jso.forEach(fields::add);

        final JsValue jso2 = JSAPI.obj(fields);

        final TestTypes.Custom val2 = codec.decode(TestTypes.Custom.class, jso2);

        Assert.assertEquals(val, val2);
    }
}
