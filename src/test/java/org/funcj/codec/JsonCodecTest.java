package org.funcj.codec;

import org.funcj.codec.TestDataBase.NoEmptyCtor;
import org.funcj.codec.json.JsonCodecCore;
import org.funcj.json.*;
import org.junit.Assert;

import java.util.Optional;

public class JsonCodecTest extends TestBase {

    final static JsonCodecCore codec = new JsonCodecCore();

    static {
        codec.registerTypeConstructor(NoEmptyCtor.class, () -> NoEmptyCtor.create(false));
        codec.registerCodec((Class)Optional.class, new OptionalCodec<Object>(codec));
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final JSValue node = codec.encode(clazz, val);

        final T val2 = codec.decode(clazz, node);

        if (!val.equals(val2)) {
            java.lang.System.out.println(node.toJson(40));
        }

        Assert.assertEquals(val, val2);
    }

    static class OptionalCodec<T> implements Codec<Optional<T>, JSValue> {

        private final JsonCodecCore core;

        OptionalCodec(JsonCodecCore  core) {
            this.core = core;
        }

        @Override
        public JSValue encode(Optional<T> val, JSValue out) {
            return val.map(t -> core.dynamicCodec().encode(t, out))
                    .orElse(JSObject.of());
        }

        @Override
        public Optional<T> decode(Class<Optional<T>> dynType, JSValue in) {
            if (in.isObject()) {
                final JSObject jso = in.asObject();
                if (jso.isEmpty()) {
                    return Optional.empty();
                }
            }

            return Optional.of((T) core.dynamicCodec().decode(in));
        }
    }
}
