package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.*;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class JsonCodecs {
    public static JsonCodecCoreImpl registerAll(JsonCodecCoreImpl core) {
        core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }

    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, JsonIO.Input, JsonIO.Output> {

        protected OptionalCodec(CodecCoreIntl<JsonIO.Input, JsonIO.Output> core) {
            super(core);
        }

        @Override
        public JsonIO.Output encode(Optional<T> val, JsonIO.Output out) {
            return val.map(t -> core.dynamicCodec().encode(t, out))
                    .orElseGet(() -> out.startObject().endObject());
        }

        @Override
        public Optional<T> decode(JsonIO.Input in) {
            if (in.currentEventType().equals(JsonIO.Input.Event.Type.OBJECT_START) &&
                    in.event(1).type().equals(JsonIO.Input.Event.Type.OBJECT_END)) {
                in.startObject();
                in.endObject();
                return Optional.empty();
            } else {
                return Optional.of((T)core.dynamicCodec().decode(in));
            }
        }
    }
}
