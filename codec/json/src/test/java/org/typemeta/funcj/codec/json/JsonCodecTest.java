package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.JsValue;

public class JsonCodecTest extends TestBase {

    final static JsonCodecCore codec = JsonCodecs.jsonCodec();

    static {
        codec.registerTypeConstructor(TestTypes.NoEmptyCtor.class, () -> TestTypes.NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws Exception {
        final JsValue node = codec.encode(clazz, val);

        final T val2 = codec.decode(clazz, node);

        if (printData || !val.equals(val2)) {
            java.lang.System.out.println(node.toString(40));
        }

        Assert.assertEquals(val, val2);
    }
}
