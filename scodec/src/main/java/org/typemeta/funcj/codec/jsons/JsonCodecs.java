package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.jsons.JsonIO.Input;
import org.typemeta.funcj.codec.jsons.JsonIO.Output;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class JsonCodecs {
    public static JsonCodecCoreImpl registerAll(JsonCodecCoreImpl core) {
        //core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }
//
//    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, Input, Output> {
//
//        protected OptionalCodec(CodecCoreIntl<Input, Output> core) {
//            super(core);
//        }
//
//        @Override
//        public Class<Optional<T>> type() {
//            return (Class<Optional<T>>)(Class)Optional.class;
//        }
//
//        @Override
//        public Output encode(Optional<T> val, Output out) {
//            return val.map(t -> core.encode(t, out))
//                    .orElseGet(() -> out.startObject().endObject());
//        }
//
//        @Override
//        public Optional<T> decode(Input in) {
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
