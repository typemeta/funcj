package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.*;

import java.util.Optional;

import static org.typemeta.funcj.util.Exceptions.*;

@SuppressWarnings("unchecked")
public class JsonCodecs {
    public static JsonCodecCoreImpl registerAll(JsonCodecCoreImpl core) {
        core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }

    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, JsValue> {

        protected OptionalCodec(CodecCoreIntl<JsValue> core) {
            super(core);
        }

        @Override
        public JsValue encode(Optional<T> val, JsValue enc) throws Exception {
            return unwrap(() -> val.map(t -> wrap(() -> core.dynamicCodec().encode(t, enc)))
                    .orElseGet(JSAPI::obj));
        }

        @Override
        public Optional<T> decode(JsValue enc) throws Exception {
            if (enc.isObject() && enc.asObject().isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of((T)core.dynamicCodec().decode(enc));
            }
        }
    }
}
