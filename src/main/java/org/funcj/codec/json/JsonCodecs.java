package org.funcj.codec.json;

import org.funcj.codec.*;
import org.funcj.json.*;

import java.util.Optional;

public class JsonCodecs {
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, JSValue> {

        protected OptionalCodec(CodecCore<JSValue> core) {
            super(core);
        }

        @Override
        public JSValue encode(Optional<T> val, JSValue enc) {
            return val.map(t -> core.dynamicCodec().encode(t, enc))
                    .orElseGet(() -> JSObject.of());
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
