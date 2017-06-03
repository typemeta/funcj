package org.funcj.codec;

import org.funcj.codec.TestDataBase.NoPublicEmptyCtor;
import org.funcj.codec.json.JsonCodecCore;
import org.funcj.json.*;
import org.junit.Assert;

public class JsonCodecTest extends TestBase {

    final static JsonCodecCore codec = new JsonCodecCore();

    static {
        codec.registerCodec(NoPublicEmptyCtor.class, new NoPublicEmptyCtorCodec());
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JSValue node = codec.encode(clazz, val);
        //java.lang.System.out.println(node.toJson(40));

        final T val2 = codec.decode(clazz, node);

        Assert.assertEquals(val, val2);
    }

    static class NoPublicEmptyCtorCodec implements Codec<NoPublicEmptyCtor, JSValue> {

        @Override
        public JSValue encode(NoPublicEmptyCtor val, JSValue out) {
            return Json.bool(val.flag);
        }

        @Override
        public NoPublicEmptyCtor decode(JSValue in) {
            return NoPublicEmptyCtor.create(in.asBool().getValue());
        }
    }
}
