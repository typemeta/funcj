package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.io.JsonIO.*;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class JsonCodecs {
    public static JsonCodecCore registerAll(JsonCodecCore core) {
        //core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }
//
//    public static class OptionalCodec<T> implements Codec<Optional<T>, Input, Output, JsonCodecConfig> {
//
//        @Override
//        public Class<Optional<T>> type() {
//            return (Class<Optional<T>>)(Class)Optional.class;
//        }
//
//        @Override
//        public Output encode(CodecCoreInternal<Input, Output, JsonCodecConfig> core, Optional<T> val, Output out) {
//            return val.map(t -> core.encode(t, out))
//                    .orElseGet(() -> out.startObject().endObject());
//        }
//
//        @Override
//        public Optional<T> decode(CodecCoreInternal<Input, Output, JsonCodecConfig> core, Input in) {
//            if (in.currentEventType().equals(Input.Event.Type.OBJECT_START) &&
//                    in.event(1).type().equals(Input.Event.Type.OBJECT_END)) {
//                in.startObject();
//                in.endObject();
//                return Optional.empty();
//            } else {
//                return Optional.of((T)core.dynamicCodec().decode(in));
//            }
//        }
//    }
}
