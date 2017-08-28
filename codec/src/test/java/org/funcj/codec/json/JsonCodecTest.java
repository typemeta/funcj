package org.funcj.codec.json;

import org.funcj.codec.*;
import org.funcj.codec.TestTypes.NoEmptyCtor;
import org.funcj.codec.json.*;
import org.funcj.json.JSValue;
import org.junit.Assert;

public class JsonCodecTest extends TestBase {

    final static JsonCodecCore codec = Codecs.jsonCodec();

    static {
        codec.registerTypeConstructor(NoEmptyCtor.class, () -> NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JSValue node = codec.encode(clazz, val);

        final T val2 = codec.decode(clazz, node);

        if (printData || !val.equals(val2)) {
            java.lang.System.out.println(node.toJson(40));
        }

        Assert.assertEquals(val, val2);
    }
}
