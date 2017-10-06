package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.*;

import java.util.Optional;

public class JsonCodecs {
    public static JsonCodecCoreImpl registerAll(JsonCodecCoreImpl core) {
        core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }

    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, JSValue> {

        protected OptionalCodec(BaseCodecCore<JSValue> core) {
            super(core);
        }

        @Override
        public JSValue encode(Optional<T> val, JSValue enc) {
            return val.map(t -> core.dynamicCodec().encode(t, enc))
                    .orElseGet(JSAPI::obj);
        }

        @Override
        public Optional<T> decode(JSValue enc) {
            if (enc.isObject() && enc.asObject().isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of((T)core.dynamicCodec().decode(enc));
            }
        }
    }
}
